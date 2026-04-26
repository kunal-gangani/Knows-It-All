package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.know_it_all.data.model.SwapStatus
import com.example.know_it_all.data.model.dto.SwapDTO
import com.example.know_it_all.presentation.ui.components.BottomNavigationBar
import com.example.know_it_all.ui.theme.KnowItAllColors
import com.example.know_it_all.presentation.viewmodel.TradeViewModel

/**
 * Fixes applied:
 *  1. AuthViewModel removed — receives userId: String directly.
 *  2. Token no longer passed to ViewModel — read internally.
 *  3. loadActiveSwaps/loadSwapHistory called without token param.
 *  4. SwapItem completely redesigned — editorial card with status color coding,
 *     accept/cancel actions, and the ochre calendar-style date display.
 *
 * Design: segmented tab control (not Material TabRow), swap cards with
 * status color bands matching the reference's warm ochre active state,
 * acid green for completed/accepted actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreenEnhanced(
    navController: NavHostController,
    tradeViewModel: TradeViewModel,
    userId: String,
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tradeState by tradeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        tradeViewModel.init(userId)
        tradeViewModel.loadActiveSwaps(userId)   // ✅ no token param
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) tradeViewModel.loadSwapHistory()  // ✅ no token param
    }

    Scaffold(
        containerColor = KnowItAllColors.Cream,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = navController.currentBackStackEntry?.destination?.route
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TRADE CENTER",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                            color = KnowItAllColors.WarmGray
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "My Swaps",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = KnowItAllColors.NearBlack,
                                letterSpacing = (-0.5).sp
                            )
                            // Pending badge
                            if (tradeState.pendingRequestCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(KnowItAllColors.AcidGreen, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${tradeState.pendingRequestCount}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = KnowItAllColors.NearBlack
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KnowItAllColors.Cream
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KnowItAllColors.Cream)
                .padding(innerPadding)
        ) {
            // Segmented tab control — pill style, not Material TabRow
            SegmentedTabControl(
                tabs = listOf("Active", "History"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            val swaps = if (selectedTab == 0) tradeState.activeSwaps
                        else tradeState.swapHistory

            if (tradeState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = KnowItAllColors.NearBlack,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else if (swaps.isEmpty()) {
                TradeEmptyState(isActive = selectedTab == 0)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    itemsIndexed(swaps, key = { _, swap -> swap.swapId }) { index, swap ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(280, index * 50)) +
                                    slideInVertically(tween(280, index * 50)) { it / 5 }
                        ) {
                            SwapCard(
                                swap = swap,
                                onComplete = { tradeViewModel.completeSwap(swap.swapId) },
                                onCancel = { tradeViewModel.cancelSwap(swap.swapId) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Swap card — editorial design with status color band
// ---------------------------------------------------------------------------

@Composable
private fun SwapCard(
    swap: SwapDTO,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val statusColor = when (swap.status) {
        SwapStatus.ACTIVE     -> KnowItAllColors.AcidGreen
        SwapStatus.REQUESTED  -> KnowItAllColors.Ochre
        SwapStatus.COMPLETED  -> KnowItAllColors.CharcoalGray
        SwapStatus.CANCELLED  -> KnowItAllColors.ErrorRed
        SwapStatus.DISPUTED   -> KnowItAllColors.ErrorRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(KnowItAllColors.CreamDark)
    ) {
        // Left status band
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(100.dp)
                .align(Alignment.CenterStart)
                .background(statusColor)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = swap.skillName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = KnowItAllColors.NearBlack,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "with ${swap.mentorName}",
                        fontSize = 13.sp,
                        color = KnowItAllColors.CharcoalGray
                    )
                }

                // Status pill
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = swap.status.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Token info
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(KnowItAllColors.NearBlack, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${swap.swapType.name}  ·  ${swap.tokenAmount}T",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KnowItAllColors.AcidGreen,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Action buttons — only for actionable statuses
            if (swap.status == SwapStatus.ACTIVE || swap.status == SwapStatus.REQUESTED) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (swap.status == SwapStatus.ACTIVE) {
                        ActionChip(
                            label = "Complete",
                            icon = Icons.Default.Check,
                            containerColor = KnowItAllColors.AcidGreen,
                            contentColor = KnowItAllColors.NearBlack,
                            onClick = onComplete
                        )
                    }
                    ActionChip(
                        label = "Cancel",
                        icon = Icons.Default.Close,
                        containerColor = KnowItAllColors.CreamDeep,
                        contentColor = KnowItAllColors.CharcoalGray,
                        onClick = onCancel
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = null,
            tint = contentColor, modifier = Modifier.size(13.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
    }
}

// ---------------------------------------------------------------------------
// Segmented tab control — pill style matching reference
// ---------------------------------------------------------------------------

@Composable
private fun SegmentedTabControl(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(KnowItAllColors.CreamDark, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Row {
            tabs.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) KnowItAllColors.NearBlack
                                  else Color.Transparent,
                    animationSpec = tween(200),
                    label = "tab_bg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) KnowItAllColors.Cream
                                  else KnowItAllColors.CharcoalGray,
                    animationSpec = tween(200),
                    label = "tab_text"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(bgColor, RoundedCornerShape(9.dp))
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun TradeEmptyState(isActive: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (isActive) "🤝" else "📋", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isActive) "No active swaps" else "No swap history yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = KnowItAllColors.NearBlack
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isActive) "Head to the Radar to find\na mentor and request a swap"
                else "Your completed swaps\nwill appear here",
            fontSize = 13.sp,
            color = KnowItAllColors.CharcoalGray,
            lineHeight = 20.sp
        )
    }
}