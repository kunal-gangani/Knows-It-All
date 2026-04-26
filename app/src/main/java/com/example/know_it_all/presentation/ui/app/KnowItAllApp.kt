package com.example.know_it_all.presentation.ui.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.know_it_all.KnowItAllApplication
import com.example.know_it_all.presentation.ui.navigation.KnowItAllNavigation

@Composable
fun KnowItAllApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as KnowItAllApplication

    Scaffold { innerPadding ->
        KnowItAllNavigation(
            navController = navController,
            application = application,
            modifier = Modifier.padding(innerPadding)
        )
    }
}