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

    /** 盈亏金额 */
    val gainLoss: Double
        get() = currentValue - purchaseValue

    /** 盈亏百分比 */
    val gainLossPercent: Double
        get() = if (purchaseValue > 0) (gainLoss / purchaseValue) * 100 else 0.0
}
