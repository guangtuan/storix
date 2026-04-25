package com.storix.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.storix.app.StorixApplication
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetCategory
import java.io.File
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

    private companion object {
        const val IMAGE_DIRECTORY = "asset-images"
    }
}
