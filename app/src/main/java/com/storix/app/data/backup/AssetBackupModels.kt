package com.storix.app.data.backup

import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetCategory

data class AssetBackupFile(
    val version: Int,
    val exportedAt: Long,
    val assets: List<AssetBackupItem>
)

data class AssetBackupItem(
    val id: Long,
    val name: String,
    val category: AssetCategory,
    val description: String,
    val currentValue: Double,
    val purchaseValue: Double,
    val isRetired: Boolean,
    val currency: String,
    val purchaseDate: Long,
    val imageUrl: String?,
    val location: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val embeddedImage: EmbeddedImage?
)

data class EmbeddedImage(
    val mimeType: String,
    val fileName: String,
    val base64Data: String
)

fun Asset.toBackupItem(embeddedImage: EmbeddedImage?): AssetBackupItem {
    return AssetBackupItem(
        id = id,
        name = name,
        category = category,
        description = description,
        currentValue = currentValue,
        purchaseValue = purchaseValue,
        isRetired = isRetired,
        currency = currency,
        purchaseDate = purchaseDate,
        imageUrl = imageUrl,
        location = location,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        embeddedImage = embeddedImage
    )
}

fun AssetBackupItem.toAsset(imageUrlOverride: String?): Asset {
    return Asset(
        id = id,
        name = name,
        category = category,
        description = description,
        currentValue = currentValue,
        purchaseValue = purchaseValue,
        isRetired = isRetired,
        currency = currency,
        purchaseDate = purchaseDate,
        imageUrl = imageUrlOverride ?: imageUrl,
        location = location,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
