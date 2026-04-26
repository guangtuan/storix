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
import com.storix.app.data.backup.toMember
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetCategory
import com.storix.app.data.local.Member
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.util.Base64
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ImportExportResult(
    val success: Boolean,
    val message: String
)

data class MemberUiState(
    val members: List<Member> = emptyList()
) {
    val defaultMember: Member?
        get() = members.firstOrNull { it.isDefault } ?: members.firstOrNull()

    val defaultMemberId: Long?
        get() = defaultMember?.id

    fun resolveMember(memberId: Long?): Member? {
        if (members.isEmpty()) {
            return null
        }
        return members.firstOrNull { it.id == memberId } ?: defaultMember
    }
}

data class HomeUiState(
    val assets: List<Asset> = emptyList(),
    val members: List<Member> = emptyList()
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

    val defaultMember: Member?
        get() = members.firstOrNull { it.isDefault } ?: members.firstOrNull()

    fun resolveMember(memberId: Long?): Member? {
        if (members.isEmpty()) {
            return null
        }
        return members.firstOrNull { it.id == memberId } ?: defaultMember
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as StorixApplication).container.assetRepository
    private val gson = Gson()

    val memberUiState = repository.observeMembers()
        .map(::MemberUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MemberUiState()
        )

    val homeUiState = combine(
        repository.observeAssets(),
        repository.observeMembers()
    ) { assets, members ->
        HomeUiState(assets = assets, members = members)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    init {
        viewModelScope.launch {
            repository.ensureDefaultMember()
        }
    }

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
        memberId: Long?,
        location: String,
        notes: String
    ) {
        val now = System.currentTimeMillis()
        val finalImageUrl = persistImageIfNeeded(
            imageUrl = imageUrl,
            existingImageUrl = existingAsset?.imageUrl,
            directory = ASSET_IMAGE_DIRECTORY
        )

        if (existingAsset?.imageUrl != finalImageUrl) {
            deleteLocalImageIfManaged(existingAsset?.imageUrl, ASSET_IMAGE_DIRECTORY)
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
                memberId = memberId,
                location = location.trim(),
                notes = notes.trim(),
                createdAt = existingAsset?.createdAt ?: now,
                updatedAt = now
            )
        )
    }

    suspend fun deleteAsset(asset: Asset) {
        repository.delete(asset)
        deleteLocalImageIfManaged(asset.imageUrl, ASSET_IMAGE_DIRECTORY)
    }

    suspend fun saveMember(existingMember: Member?, name: String, avatarUrl: String?) {
        val normalizedName = name.trim()
        if (normalizedName.isBlank()) {
            return
        }

        val now = System.currentTimeMillis()
        val finalAvatarUrl = persistImageIfNeeded(
            imageUrl = avatarUrl,
            existingImageUrl = existingMember?.avatarUrl,
            directory = MEMBER_IMAGE_DIRECTORY
        )

        if (existingMember?.avatarUrl != finalAvatarUrl) {
            deleteLocalImageIfManaged(existingMember?.avatarUrl, MEMBER_IMAGE_DIRECTORY)
        }

        repository.upsertMember(
            Member(
                id = existingMember?.id ?: 0,
                name = normalizedName,
                avatarUrl = finalAvatarUrl,
                isDefault = existingMember?.isDefault ?: false,
                createdAt = existingMember?.createdAt ?: now,
                updatedAt = now
            )
        )
        repository.ensureDefaultMember()
    }

    suspend fun deleteMember(member: Member): Boolean {
        if (member.isDefault) {
            return false
        }

        repository.deleteMember(member)
        deleteLocalImageIfManaged(member.avatarUrl, MEMBER_IMAGE_DIRECTORY)
        repository.ensureDefaultMember()
        return true
    }

    suspend fun setDefaultMember(memberId: Long) {
        repository.setDefaultMember(memberId)
    }

    suspend fun exportAssetsToUri(uri: Uri): ImportExportResult {
        return runCatching {
            val app = getApplication<Application>()
            val assets = repository.getAllAssets()
            val members = repository.getAllMembers()
            val backup = AssetBackupFile(
                version = BackupFormatVersion,
                exportedAt = System.currentTimeMillis(),
                members = members.map { member ->
                    val embeddedAvatar = loadManagedImageAsBase64(member.avatarUrl, MEMBER_IMAGE_DIRECTORY)
                    member.toBackupItem(embeddedAvatar)
                },
                assets = assets.map { asset ->
                    val embeddedImage = loadManagedImageAsBase64(asset.imageUrl, ASSET_IMAGE_DIRECTORY)
                    asset.toBackupItem(embeddedImage)
                }
            )

            app.contentResolver.openOutputStream(uri)?.use { output ->
                output.writer().use { writer ->
                    gson.toJson(backup, writer)
                }
            } ?: error("无法写入导出文件")

            ImportExportResult(
                success = true,
                message = "导出成功，共 ${assets.size} 条资产，${members.size} 位成员"
            )
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

            val importedMembers = if (backup.version >= MemberBackupFormatVersion) {
                backup.members.orEmpty().map { item ->
                    val restoredAvatarUrl = restoreEmbeddedImage(
                        embeddedImage = item.embeddedAvatar,
                        directory = MEMBER_IMAGE_DIRECTORY
                    )
                    item.toMember(avatarUrlOverride = restoredAvatarUrl)
                }
            } else {
                emptyList()
            }

            val importedAssets = backup.assets.map { item ->
                val restoredImageUrl = restoreEmbeddedImage(
                    embeddedImage = item.embeddedImage,
                    directory = ASSET_IMAGE_DIRECTORY
                )
                item.toAsset(imageUrlOverride = restoredImageUrl)
            }

            if (backup.version >= MemberBackupFormatVersion) {
                repository.replaceMembers(importedMembers)
            }
            repository.replaceAssets(importedAssets)
            repository.ensureDefaultMember()
            ImportExportResult(
                success = true,
                message = "导入成功，共 ${importedAssets.size} 条资产，${importedMembers.size} 位成员"
            )
        }.getOrElse { throwable ->
            val message = when (throwable) {
                is JsonSyntaxException -> "备份文件格式不正确"
                else -> throwable.message ?: "未知错误"
            }
            ImportExportResult(success = false, message = "导入失败：$message")
        }
    }

    private fun persistImageIfNeeded(
        imageUrl: String?,
        existingImageUrl: String?,
        directory: String
    ): String? {
        val normalizedImageUrl = imageUrl?.trim()?.ifBlank { null } ?: return null
        if (!normalizedImageUrl.startsWith("content://")) {
            return normalizedImageUrl
        }
        if (normalizedImageUrl == existingImageUrl) {
            return existingImageUrl
        }

        val app = getApplication<Application>()
        val imagesDir = File(app.filesDir, directory).apply { mkdirs() }
        val targetFile = File(imagesDir, "${UUID.randomUUID()}.jpg")

        app.contentResolver.openInputStream(Uri.parse(normalizedImageUrl))?.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return existingImageUrl

        return Uri.fromFile(targetFile).toString()
    }

    private fun deleteLocalImageIfManaged(imageUrl: String?, directory: String) {
        val normalizedImageUrl = imageUrl?.trim()?.ifBlank { null } ?: return
        val uri = Uri.parse(normalizedImageUrl)
        if (uri.scheme != "file") {
            return
        }

        val app = getApplication<Application>()
        val imageDirectory = File(app.filesDir, directory).canonicalFile
        val imagePath = uri.path ?: return
        val imageFile = File(imagePath).canonicalFile
        if (imageFile.parentFile == imageDirectory && imageFile.exists()) {
            imageFile.delete()
        }
    }

    private fun validateBackupFile(backup: AssetBackupFile) {
        if (backup.version !in SupportedBackupVersions) {
            error("不支持的备份版本：${backup.version}")
        }
    }

    private fun loadManagedImageAsBase64(imageUrl: String?, directory: String): EmbeddedImage? {
        val imageFile = managedImageFile(imageUrl, directory) ?: return null
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

    private fun restoreEmbeddedImage(embeddedImage: EmbeddedImage?, directory: String): String? {
        if (embeddedImage == null) {
            return null
        }

        val app = getApplication<Application>()
        val imagesDir = File(app.filesDir, directory).apply { mkdirs() }
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

    private fun managedImageFile(imageUrl: String?, directory: String): File? {
        val normalizedImageUrl = imageUrl?.trim()?.ifBlank { null } ?: return null
        val uri = Uri.parse(normalizedImageUrl)
        if (uri.scheme != "file") {
            return null
        }

        val app = getApplication<Application>()
        val imageDirectory = File(app.filesDir, directory).canonicalFile
        val imagePath = uri.path ?: return null
        val imageFile = File(imagePath).canonicalFile
        if (imageFile.parentFile != imageDirectory) {
            return null
        }
        return imageFile
    }

    private companion object {
        const val ASSET_IMAGE_DIRECTORY = "asset-images"
        const val MEMBER_IMAGE_DIRECTORY = "member-avatars"
        const val LegacyBackupFormatVersion = 1
        const val BackupFormatVersion = 2
        const val MemberBackupFormatVersion = 2
        val SupportedBackupVersions = setOf(LegacyBackupFormatVersion, BackupFormatVersion)
        const val DefaultImageMimeType = "image/jpeg"
    }
}
