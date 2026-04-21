package com.storix.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY currentValue DESC, updatedAt DESC")
    fun observeAssets(): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE id = :assetId LIMIT 1")
    fun observeAsset(assetId: Long): Flow<Asset?>

    @Upsert
    suspend fun upsert(asset: Asset)

    @Delete
    suspend fun delete(asset: Asset)
}
