package com.example.know_it_all

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.know_it_all.data.local.prefs.PreferenceManager
import com.example.know_it_all.data.remote.RetrofitClient
import com.example.know_it_all.presentation.ui.navigation.KnowItAllNavigation
import com.example.know_it_all.ui.theme.KnowItAllTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefManager = PreferenceManager(this)
        RetrofitClient.init(prefManager)

        enableEdgeToEdge()
        setContent {
            KnowItAllTheme {
                KnowItAllNavigation()
            }
        }
    }
}