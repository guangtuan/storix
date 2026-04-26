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

private val Migration2To3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE assets ADD COLUMN memberId INTEGER")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS members (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                avatarUrl TEXT,
                isDefault INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        val now = System.currentTimeMillis()
        database.execSQL(
            "INSERT INTO members (name, avatarUrl, isDefault, createdAt, updatedAt) VALUES ('默认成员', NULL, 1, $now, $now)"
        )
    }
}

class AppContainer(context: Context) {
    private val database: StorixDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            StorixDatabase::class.java,
            "storix.db"
        )
            .addMigrations(Migration1To2, Migration2To3)
            .fallbackToDestructiveMigration()
            .build()
    }

    private val publicImageApi: PublicImageApi by lazy {
        PublicImageApi.create()
    }

    val assetRepository: AssetRepository by lazy {
        AssetRepository(
            assetDao = database.assetDao(),
            memberDao = database.memberDao(),
            publicImageApi = publicImageApi
        )
    }
}
