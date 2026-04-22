package com.storix.app.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.storix.app.data.local.StorixDatabase
import com.storix.app.data.remote.PublicImageApi
import com.storix.app.data.repository.AssetRepository

private val Migration1To2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE assets ADD COLUMN isRetired INTEGER NOT NULL DEFAULT 0")
    }
}

class AppContainer(context: Context) {
    private val database: StorixDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            StorixDatabase::class.java,
            "storix.db"
        )
            .addMigrations(Migration1To2)
            .fallbackToDestructiveMigration()
            .build()
    }

    private val publicImageApi: PublicImageApi by lazy {
        PublicImageApi.create()
    }

    val assetRepository: AssetRepository by lazy {
        AssetRepository(
            assetDao = database.assetDao(),
            publicImageApi = publicImageApi
        )
    }
}
