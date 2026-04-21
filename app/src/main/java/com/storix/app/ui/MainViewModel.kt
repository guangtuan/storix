package com.storix.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.storix.app.StorixApplication
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val assets: List<Asset> = emptyList()
) {
    val assetCount: Int
        get() = assets.size

    val totalValue: Double
        get() = assets.sumOf { it.currentValue }

    val totalCost: Double
        get() = assets.sumOf { it.purchaseValue }

    val totalGain: Double
        get() = totalValue - totalCost
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
        currentValue: Double,
        purchaseValue: Double,
        purchaseDate: Long,
        currency: String,
        imageUrl: String?,
        location: String,
        notes: String
    ) {
        val now = System.currentTimeMillis()
        repository.upsert(
            Asset(
                id = existingAsset?.id ?: 0,
                name = name.trim(),
                category = category,
                description = description.trim(),
                currentValue = currentValue,
                purchaseValue = purchaseValue,
                purchaseDate = purchaseDate,
                currency = currency.trim().uppercase().ifBlank { "CNY" },
                imageUrl = imageUrl?.trim()?.ifBlank { null },
                location = location.trim(),
                notes = notes.trim(),
                createdAt = existingAsset?.createdAt ?: now,
                updatedAt = now
            )
        )
    }

    suspend fun deleteAsset(asset: Asset) {
        repository.delete(asset)
    }
}
