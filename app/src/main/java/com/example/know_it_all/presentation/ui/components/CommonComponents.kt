package com.example.know_it_all.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInUp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.know_it_all.presentation.ui.navigation.BottomNavItem
import com.example.know_it_all.presentation.ui.navigation.Screen

// ===== ENHANCED BOTTOM NAVIGATION BAR =====
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        BottomNavItem.items().forEach { item ->
            NavigationBarItem(
                icon = { 
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (currentRoute == item.route) 
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else 
                                    MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.name,
                            tint = if (currentRoute == item.route)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { 
                    Text(
                        item.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (currentRoute == item.route) FontWeight.SemiBold else FontWeight.Normal
                    ) 
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

// ===== ENHANCED USER CARD WITH MODERN DESIGN =====
@Composable
fun UserCard(
    name: String,
    distance: String,
    trustScore: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 4.dp
        ),
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📍",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = distance,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⭐ ${String.format("%.1f", trustScore)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ===== MODERN SKILL BADGE =====
@Composable
fun SkillBadge(skillName: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "✨",
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = skillName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ===== PREMIUM TOKEN BALANCE CARD WITH GRADIENT EFFECT =====
@Composable
fun TokenBalanceCard(balance: Long, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "💎 SKILL TOKENS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = balance.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Available for trading",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

// ===== MODERN STATUS BADGE WITH ANIMATIONS =====
@Composable
fun SwapStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor, icon) = when (status.uppercase()) {
        "ACTIVE" -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.secondary,
            "🔄"
        )
        "COMPLETED" -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.tertiary,
            "✓"
        )
        "REQUESTED" -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.primary,
            "⏳"
        )
        else -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.error,
            "⚠"
        )
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ===== NEW: GRADIENT HEADER SECTION =====
@Composable
fun GradientHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = androidx.compose.foundation.background.Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(vertical = 24.dp, horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// ===== NEW: MODERN PRIMARY BUTTON =====
@Composable
fun ModernButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ===== NEW: MODERN SECONDARY BUTTON =====
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
