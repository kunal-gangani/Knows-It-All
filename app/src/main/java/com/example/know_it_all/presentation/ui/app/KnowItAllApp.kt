package com.example.know_it_all.presentation.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.know_it_all.presentation.ui.navigation.KnowItAllNavigation

@Composable
fun KnowItAllApp(modifier: Modifier = Modifier) {
    KnowItAllNavigation(modifier = modifier)
}
