package com.storix.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: AssetCategory,
    val description: String = "",
    val currentValue: Double,
    val purchaseValue: Double,
    val isRetired: Boolean = false,
    val currency: String = "CNY",
    val purchaseDate: Long = System.currentTimeMillis(),
    val imageUrl: String? = null,
    val location: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /** 持有天数 */
    val holdingDays: Long
        get() = (System.currentTimeMillis() - purchaseDate) / (1000 * 60 * 60 * 24)
}
