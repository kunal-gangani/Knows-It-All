package com.example.know_it_all.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.navigation.BottomNavItem
import com.example.know_it_all.ui.theme.AcidGreen
import com.example.know_it_all.ui.theme.CharcoalGray
import com.example.know_it_all.ui.theme.Cream
import com.example.know_it_all.ui.theme.CreamDark
import com.example.know_it_all.ui.theme.NearBlack

/**
 * Bottom navigation bar used by all four main screens.
 *
 * Design matches the Dribbble reference:
 *  - Cream background, near-black icons
 *  - Active item gets an acid-green pill indicator above the icon
 *  - No labels (icon-only, minimal — matches the reference's bottom nav style)
 *  - Smooth animated color transition on tab switch (200ms)
 *
 * Uses BottomNavItem.items() to build the tab list so adding a new
 * destination only requires adding it to BottomNavItem — not here.
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CreamDark)
    ) {
        // Top divider line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Cream.copy(alpha = 0.6f))
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.items().forEach { item ->
                val isSelected = currentRoute == item.route
                BottomNavTab(
                    item = item,
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                // Pop up to the first item in the back stack
                                // so back-pressing from any tab goes to Radar
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavTab(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) NearBlack else CharcoalGray,
        animationSpec = tween(200),
        label = "nav_icon_color"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Acid green active indicator pill
        Box(
            modifier = Modifier
                .size(width = 20.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isSelected) AcidGreen else Color.Transparent)
        )

        Icon(
            imageVector = item.icon,
            contentDescription = item.name,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )

        // Label shown only for active tab
        if (isSelected) {
            Text(
                text = item.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = NearBlack,
                letterSpacing = 0.5.sp
            )
        }
    }
}