package com.storix.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.storix.app.StorixApplication
import com.storix.app.data.backup.AssetBackupFile
import com.storix.app.data.backup.EmbeddedImage
import com.storix.app.data.backup.toAsset
import com.storix.app.data.backup.toBackupItem
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetCategory
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.util.Base64
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ImportExportResult(
    val success: Boolean,
    val message: String
)

data class HomeUiState(
    val assets: List<Asset> = emptyList()
) {
    val activeAssetCount: Int
        get() = assets.count { !it.isRetired }

    val retiredAssetCount: Int
        get() = assets.count { it.isRetired }

    val totalOriginalCost: Double
        get() = assets.sumOf { it.purchaseValue }

    val activeOriginalCost: Double
        get() = assets.filterNot { it.isRetired }.sumOf { it.purchaseValue }

    /** The active asset that has accompanied the user the longest (earliest purchase date). */
    val longestCompanionAsset: Asset?
        get() = assets.filterNot { it.isRetired }.minByOrNull { it.purchaseDate }

    /** The most recently added asset (latest purchase date). */
    val newestAsset: Asset?
        get() = assets.maxByOrNull { it.purchaseDate }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as StorixApplication).container.assetRepository
    private val gson = Gson()

    val homeUiState = repository.observeAssets()
        .map(::HomeUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun observeAsset(assetId: Long): Flow<Asset?> = repository.observeAsset(assetId)

    suspend fun searchPublicImage(query: String): String? = repository.searchPublicImage(query)

    suspend fun saveAsset(
        existingAsset: Asset?,
        name: String,
        category: AssetCategory,
        description: String,
        purchaseValue: Double,
        isRetired: Boolean,
        purchaseDate: Long,
        currency: String,
        imageUrl: String?,
        location: String,
        notes: String
    ) {
        val now = System.currentTimeMillis()
        val finalImageUrl = persistImageIfNeeded(
            imageUrl = imageUrl,
            existingImageUrl = existingAsset?.imageUrl
        )

        if (existingAsset?.imageUrl != finalImageUrl) {
            deleteLocalImageIfManaged(existingAsset?.imageUrl)
        }

        repository.upsert(
            Asset(
                id = existingAsset?.id ?: 0,
                name = name.trim(),
                category = category,
                description = description.trim(),
                currentValue = purchaseValue,
                purchaseValue = purchaseValue,
                isRetired = isRetired,
                purchaseDate = purchaseDate,
                currency = currency.trim().uppercase().ifBlank { "CNY" },
                imageUrl = finalImageUrl,
                location = location.trim(),
                notes = notes.trim(),
                createdAt = existingAsset?.createdAt ?: now,
                updatedAt = now
            )
        )
    }

    suspend fun deleteAsset(asset: Asset) {
        repository.delete(asset)
        deleteLocalImageIfManaged(asset.imageUrl)
    }

    suspend fun exportAssetsToUri(uri: Uri): ImportExportResult {
        return runCatching {
            val app = getApplication<Application>()
            val assets = repository.getAllAssets()
            val backup = AssetBackupFile(
                version = BackupFormatVersion,
                exportedAt = System.currentTimeMillis(),
                assets = assets.map { asset ->
                    val embeddedImage = loadManagedImageAsBase64(asset.imageUrl)
                    asset.toBackupItem(embeddedImage)
                }
            )

            app.contentResolver.openOutputStream(uri)?.use { output ->
                output.writer().use { writer ->
                    gson.toJson(backup, writer)
                }
            } ?: error("无法写入导出文件")

            ImportExportResult(success = true, message = "导出成功，共 ${assets.size} 条资产")
        }.getOrElse { throwable ->
            ImportExportResult(success = false, message = "导出失败：${throwable.message ?: "未知错误"}")
        }
    }

    suspend fun importAssetsFromUri(uri: Uri): ImportExportResult {
        return runCatching {
            val app = getApplication<Application>()
            val backup = app.contentResolver.openInputStream(uri)?.use { input ->
                input.reader().use { reader ->
                    gson.fromJson(reader, AssetBackupFile::class.java)
                }
            } ?: error("无法读取导入文件")

            validateBackupFile(backup)

            val importedAssets = backup.assets.map { item ->
                val restoredImageUrl = restoreEmbeddedImage(item.embeddedImage)
                item.toAsset(imageUrlOverride = restoredImageUrl)
            }

            repository.replaceAssets(importedAssets)
            ImportExportResult(success = true, message = "导入成功，共 ${importedAssets.size} 条资产")
        }.getOrElse { throwable ->
            val message = when (throwable) {
                is JsonSyntaxException -> "备份文件格式不正确"
                else -> throwable.message ?: "未知错误"
            }
            ImportExportResult(success = false, message = "导入失败：$message")
        }
    }

    private fun persistImageIfNeeded(imageUrl: String?, existingImageUrl: String?): String? {
        val normalizedImageUrl = imageUrl?.trim()?.ifBlank { null } ?: return null
        if (!normalizedImageUrl.startsWith("content://")) {
            return normalizedImageUrl
        }
        if (normalizedImageUrl == existingImageUrl) {
            return existingImageUrl
        }

        val app = getApplication<Application>()
        val imagesDir = File(app.filesDir, IMAGE_DIRECTORY).apply { mkdirs() }
        val targetFile = File(imagesDir, "${UUID.randomUUID()}.jpg")

        app.contentResolver.openInputStream(Uri.parse(normalizedImageUrl))?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return existingImageUrl

        return Uri.fromFile(targetFile).toString()
    }

    private fun deleteLocalImageIfManaged(imageUrl: String?) {
        val normalizedImageUrl = imageUrl?.trim()?.ifBlank { null } ?: return
        val uri = Uri.parse(normalizedImageUrl)
        if (uri.scheme != "file") {
            return
        }

        val app = getApplication<Application>()
        val imageDirectory = File(app.filesDir, IMAGE_DIRECTORY).canonicalFile
        val imagePath = uri.path ?: return
        val imageFile = File(imagePath).canonicalFile
        if (imageFile.parentFile == imageDirectory && imageFile.exists()) {
            imageFile.delete()
        }
    }

    private fun validateBackupFile(backup: AssetBackupFile) {
        if (backup.version != BackupFormatVersion) {
            error("不支持的备份版本：${backup.version}")
        }
    }

    private fun loadManagedImageAsBase64(imageUrl: String?): EmbeddedImage? {
        val imageFile = managedImageFile(imageUrl) ?: return null
        if (!imageFile.exists()) {
            return null
        }

        val contentResolver = getApplication<Application>().contentResolver
        val mimeType = contentResolver.getType(Uri.fromFile(imageFile)) ?: DefaultImageMimeType
        val base64Data = Base64.getEncoder().encodeToString(imageFile.readBytes())
        return EmbeddedImage(
            mimeType = mimeType,
            fileName = imageFile.name,
            base64Data = base64Data
        )
    }

    private fun restoreEmbeddedImage(embeddedImage: EmbeddedImage?): String? {
        if (embeddedImage == null) {
            return null
        }

        val app = getApplication<Application>()
        val imagesDir = File(app.filesDir, IMAGE_DIRECTORY).apply { mkdirs() }
        val extension = extensionFromMimeType(embeddedImage.mimeType)
        val targetFile = File(imagesDir, "${UUID.randomUUID()}.$extension")
        val imageBytes = Base64.getDecoder().decode(embeddedImage.base64Data)
        targetFile.writeBytes(imageBytes)
        return Uri.fromFile(targetFile).toString()
    }

    private fun extensionFromMimeType(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
    }

    private fun managedImageFile(imageUrl: String?): File? {
        val normalizedImageUrl = imageUrl?.trim()?.ifBlank { null } ?: return null
        val uri = Uri.parse(normalizedImageUrl)
        if (uri.scheme != "file") {
            return null
        }

        val app = getApplication<Application>()
        val imageDirectory = File(app.filesDir, IMAGE_DIRECTORY).canonicalFile
        val imagePath = uri.path ?: return null
        val imageFile = File(imagePath).canonicalFile
        if (imageFile.parentFile != imageDirectory) {
            return null
        }
        return imageFile
    }

    private companion object {
        const val IMAGE_DIRECTORY = "asset-images"
        const val BackupFormatVersion = 1
        const val DefaultImageMimeType = "image/jpeg"
    }
}
