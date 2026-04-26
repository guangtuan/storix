package com.storix.app.data.repository

import android.net.Uri
import com.storix.app.data.local.Asset
import com.storix.app.data.local.AssetDao
import com.storix.app.data.remote.PublicImageApi
import kotlinx.coroutines.flow.Flow

class AssetRepository(
    private val assetDao: AssetDao,
    private val publicImageApi: PublicImageApi
) {
    fun observeAssets(): Flow<List<Asset>> = assetDao.observeAssets()

    fun observeAsset(assetId: Long): Flow<Asset?> = assetDao.observeAsset(assetId)

    suspend fun upsert(asset: Asset) {
        assetDao.upsert(asset)
    }

    suspend fun delete(asset: Asset) {
        assetDao.delete(asset)
    }

    suspend fun getAllAssets(): List<Asset> = assetDao.getAllAssets()

    suspend fun replaceAssets(assets: List<Asset>) {
        assetDao.clearAll()
        if (assets.isNotEmpty()) {
            assetDao.upsertAll(assets)
        }
    }

    suspend fun searchPublicImage(query: String): String? {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) {
            return null
        }

        val languages = listOf("zh", "en")
        for (language in languages) {
            val title = runCatching {
                publicImageApi.search(searchUrl(language, normalizedQuery))
                    .query
                    ?.search
                    ?.firstOrNull()
                    ?.title
                    ?.takeIf { it.isNotBlank() }
            }.getOrNull() ?: continue

            val imageUrl = runCatching {
                val summary = publicImageApi.summary(summaryUrl(language, title))
                summary.originalImage?.source ?: summary.thumbnail?.source
            }.getOrNull()

            if (!imageUrl.isNullOrBlank()) {
                return imageUrl
            }
        }

        return null
    }

    private fun searchUrl(language: String, query: String): String {
        return "https://$language.wikipedia.org/w/api.php?action=query&list=search&srlimit=1&format=json&srsearch=${Uri.encode(query)}"
    }

    private fun summaryUrl(language: String, title: String): String {
        return "https://$language.wikipedia.org/api/rest_v1/page/summary/${Uri.encode(title)}"
    }
}
