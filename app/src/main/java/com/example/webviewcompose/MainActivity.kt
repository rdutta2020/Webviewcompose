package com.example.webviewcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("LoginScreen", "MainActivity onCreate starts")
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                WebViewScreen(url = "https://www.google.com")
            }
        }
        Log.i("LoginScreen", "MainActivity onCreate ends")
    }
}
