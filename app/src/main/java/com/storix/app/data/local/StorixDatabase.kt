package com.storix.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Asset::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StorixDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
}
