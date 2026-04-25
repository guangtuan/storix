package com.storix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.storix.app.ui.MainViewModel
import com.storix.app.ui.StorixApp
import com.storix.app.ui.theme.ThemePreset
import com.storix.app.ui.theme.StorixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        setContent {
            var selectedTheme by remember {
                mutableStateOf(
                    ThemePreset.entries.firstOrNull {
                        it.name == preferences.getString(THEME_KEY, ThemePreset.MANJARO.name)
                    } ?: ThemePreset.MANJARO
                )
            }

            StorixTheme(themePreset = selectedTheme) {
                StorixApp(
                    viewModel = viewModel(),
                    selectedThemePreset = selectedTheme,
                    onThemePresetChange = { preset ->
                        selectedTheme = preset
                        preferences.edit().putString(THEME_KEY, preset.name).apply()
                    }
                )
            }
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "storix_settings"
        const val THEME_KEY = "theme_preset"
    }
}
