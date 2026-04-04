package com.example.know_it_all

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.know_it_all.presentation.ui.app.KnowItAllApp
import com.example.know_it_all.ui.theme.KnowItAllTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KnowItAllTheme {
                KnowItAllApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}