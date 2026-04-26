package com.example.know_it_all

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.know_it_all.presentation.ui.navigation.KnowItAllNavigation
import com.example.know_it_all.ui.theme.KnowItAllTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KnowItAllTheme {
                KnowItAllNavigation()  // no modifier param — NavGraph doesn't require it
            }
        }
    }
}