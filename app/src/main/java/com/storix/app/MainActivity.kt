package com.storix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.storix.app.ui.MainViewModel
import com.storix.app.ui.StorixApp
import com.storix.app.ui.theme.StorixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StorixTheme {
                StorixApp(viewModel = viewModel())
            }
        }
    }
}
