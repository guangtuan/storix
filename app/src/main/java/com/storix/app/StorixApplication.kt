package com.storix.app

import android.app.Application
import com.storix.app.data.AppContainer

class StorixApplication : Application() {
    val container: AppContainer by lazy {
        AppContainer(this)
    }
}
