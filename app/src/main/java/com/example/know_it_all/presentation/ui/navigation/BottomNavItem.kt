package com.example.know_it_all.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
) {
    object Radar : BottomNavItem("Radar", Screen.Radar.route, Icons.Default.LocationOn)
    object Trade : BottomNavItem("Trade", Screen.Trade.route, Icons.Default.SwapHoriz)
    object Vault : BottomNavItem("Vault", Screen.Vault.route, Icons.Default.Wallet)
    object Profile : BottomNavItem("Profile", Screen.SkillProfile.route, Icons.Default.Home)

    companion object {
        fun items() = listOf(Radar, Trade, Vault, Profile)
    }
}
