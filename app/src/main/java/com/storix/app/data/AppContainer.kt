package com.storix.app.data

import android.content.Context
import androidx.room.Room
import com.storix.app.data.local.StorixDatabase
import com.storix.app.data.remote.PublicImageApi
import com.storix.app.data.repository.AssetRepository

class AppContainer(context: Context) {
    private val database: StorixDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            StorixDatabase::class.java,
            "storix.db"
        ).fallbackToDestructiveMigration().build()
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
