package com.storix.app.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromAssetCategory(category: AssetCategory): String = category.name

    @TypeConverter
    fun toAssetCategory(name: String): AssetCategory = AssetCategory.valueOf(name)
}
