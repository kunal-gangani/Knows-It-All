package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.know_it_all.data.model.SkillCategory
import com.example.know_it_all.data.repository.FeedItem
import com.example.know_it_all.presentation.ui.components.BottomNavigationBar
import com.example.know_it_all.presentation.viewmodel.FeedViewModel
import com.example.know_it_all.ui.theme.AcidGreen
import com.example.know_it_all.ui.theme.CharcoalGray
import com.example.know_it_all.ui.theme.Cream
import com.example.know_it_all.ui.theme.CreamDark
import com.example.know_it_all.ui.theme.CreamDeep
import com.example.know_it_all.ui.theme.ErrorContainerColor
import com.example.know_it_all.ui.theme.ErrorRed
import com.example.know_it_all.ui.theme.NearBlack
import com.example.know_it_all.ui.theme.Ochre
import com.example.know_it_all.ui.theme.WarmGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavHostController,
    feedViewModel: FeedViewModel,
    onMentorConnect: (userId: String) -> Unit = {}
) {
    val feedState by feedViewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Cream,
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
                            "DISCOVER",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                            color = WarmGray
                        )
                        Text(
                            "Skill Feed",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = NearBlack,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream),
                actions = {
                    IconButton(onClick = { feedViewModel.refresh() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = if (feedState.isRefreshing) AcidGreen else NearBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            feedState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = NearBlack,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(32.dp)
                        )
                        Text("Loading feed...", fontSize = 14.sp, color = CharcoalGray)
                    }
                }
            }

            feedState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("😕", fontSize = 40.sp)
                        Text(
                            "Couldn't load feed",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NearBlack
                        )
                        Text(
                            feedState.error ?: "",
                            fontSize = 13.sp,
                            color = CharcoalGray
                        )
                        Box(
                            modifier = Modifier
                                .background(NearBlack, RoundedCornerShape(12.dp))
                                .clickable { feedViewModel.loadFeed() }
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text("Try Again", fontSize = 14.sp,
                                fontWeight = FontWeight.Bold, color = Cream)
                        }
                    }
                }
            }

            feedState.items.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌱", fontSize = 40.sp)
                        Text(
                            "Feed is empty",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NearBlack
                        )
                        Text(
                            "Be the first to add a skill\nand start trading!",
                            fontSize = 13.sp,
                            color = CharcoalGray
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Trending categories horizontal strip — always at top
                    val trending = feedState.items
                        .filterIsInstance<FeedItem.TrendingCategory>()
                    if (trending.isNotEmpty()) {
                        item {
                            TrendingCategoriesStrip(categories = trending)
                        }
                    }

                    // Rest of feed items (excluding trending shown above)
                    val mainItems = feedState.items
                        .filterNot { it is FeedItem.TrendingCategory }

                    itemsIndexed(
                        mainItems,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300, index * 40)) +
                                    slideInVertically(tween(300, index * 40)) { it / 6 }
                        ) {
                            when (item) {
                                is FeedItem.NewSkill ->
                                    NewSkillCard(item = item)
                                is FeedItem.CompletedSwap ->
                                    CompletedSwapCard(item = item)
                                is FeedItem.TopMentor ->
                                    TopMentorCard(
                                        item = item,
                                        onConnect = { onMentorConnect(item.user.uid) }
                                    )
                                else -> {}
                            }
                        }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

// =============================================================================
// Trending categories strip
// =============================================================================

@Composable
private fun TrendingCategoriesStrip(categories: List<FeedItem.TrendingCategory>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "TRENDING",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = WarmGray,
            letterSpacing = 1.sp
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(categories, key = { it.id }) { item ->
                TrendingCategoryChip(item = item)
            }
        }
    }
}

@Composable
private fun TrendingCategoryChip(item: FeedItem.TrendingCategory) {
    val (emoji, color) = when (item.category) {
        SkillCategory.DIGITAL  -> "💻" to AcidGreen
        SkillCategory.PHYSICAL -> "🔨" to Ochre
        SkillCategory.HYBRID   -> "⚡" to CharcoalGray
    }

    Column(
        modifier = Modifier
            .background(NearBlack, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(
            item.category.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            "${item.skillCount} skills",
            fontSize = 10.sp,
            color = WarmGray
        )
        Text(
            "${item.activeUserCount} people",
            fontSize = 10.sp,
            color = WarmGray
        )
    }
}

// =============================================================================
// New skill card
// =============================================================================

@Composable
private fun NewSkillCard(item: FeedItem.NewSkill) {
    val categoryColor = when (item.skill.category) {
        SkillCategory.DIGITAL  -> AcidGreen
        SkillCategory.PHYSICAL -> Ochre
        SkillCategory.HYBRID   -> CharcoalGray
    }
    val timeAgo = timeAgoString(item.timestamp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CreamDark, RoundedCornerShape(16.dp))
    ) {
        // Category color top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(categoryColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(NearBlack, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            item.userName.take(1).uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = AcidGreen
                        )
                    }
                    Column {
                        Text(
                            item.userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearBlack
                        )
                        Text(
                            "★ ${String.format("%.1f", item.userTrustScore)}",
                            fontSize = 11.sp,
                            color = Ochre
                        )
                    }
                }
                Text(timeAgo, fontSize = 11.sp, color = WarmGray)
            }

            // Skill info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(categoryColor, CircleShape)
                )
                Text(
                    "Added a new skill",
                    fontSize = 12.sp,
                    color = CharcoalGray
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NearBlack, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            item.skill.skillName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Cream,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            "${item.skill.proficiencyLevel.name} · ${item.skill.category.name}",
                            fontSize = 11.sp,
                            color = WarmGray
                        )
                        if (item.skill.description.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                item.skill.description,
                                fontSize = 12.sp,
                                color = WarmGray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(categoryColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "${item.skill.tokenValue}T",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearBlack
                            )
                        }
                        if (item.skill.endorsements > 0) {
                            Text(
                                "★ ${item.skill.endorsements}",
                                fontSize = 10.sp,
                                color = Ochre
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// Completed swap card
// =============================================================================

@Composable
private fun CompletedSwapCard(item: FeedItem.CompletedSwap) {
    val stars = "★".repeat(item.rating.toInt()) + "☆".repeat(5 - item.rating.toInt())
    val timeAgo = timeAgoString(item.timestamp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CreamDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🤝", fontSize = 18.sp)
                    Text(
                        "Swap Completed",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalGray
                    )
                }
                Text(timeAgo, fontSize = 11.sp, color = WarmGray)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mentor avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(NearBlack, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        item.mentorName.take(1).uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = AcidGreen
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        item.skillName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = NearBlack,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        "${item.mentorName} taught ${item.learnerName}",
                        fontSize = 12.sp,
                        color = CharcoalGray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        stars,
                        fontSize = 12.sp,
                        color = Ochre
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                if (item.swapType == "TOKEN") NearBlack
                                else AcidGreen.copy(alpha = 0.15f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            item.swapType,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.swapType == "TOKEN") AcidGreen else NearBlack,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// Top mentor card
// =============================================================================

@Composable
private fun TopMentorCard(
    item: FeedItem.TopMentor,
    onConnect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NearBlack, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("⭐", fontSize = 14.sp)
                Text(
                    "TOP MENTOR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Ochre,
                    letterSpacing = 1.5.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(AcidGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        item.user.name.take(1).uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NearBlack
                    )
                }
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            item.user.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Cream,
                            letterSpacing = (-0.3).sp
                        )
                        if (item.user.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AcidGreen, CircleShape)
                            )
                        }
                    }
                    Text(
                        item.topSkillName,
                        fontSize = 13.sp,
                        color = WarmGray
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatBadge("★ ${String.format("%.1f", item.user.trustScore)}", Ochre)
                        StatBadge("${item.completedSwapCount} swaps", AcidGreen)
                        StatBadge("${item.user.skillTokenBalance}T", WarmGray)
                    }
                }
            }

            // Connect button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AcidGreen, RoundedCornerShape(10.dp))
                    .clickable(onClick = onConnect)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Connect with ${item.user.name.split(" ").first()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearBlack
                )
            }
        }
    }
}

@Composable
private fun StatBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = color
    )
}

// =============================================================================
// Helpers
// =============================================================================

private fun timeAgoString(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000L              -> "just now"
        diff < 3_600_000L           -> "${diff / 60_000}m ago"
        diff < 86_400_000L          -> "${diff / 3_600_000}h ago"
        diff < 604_800_000L         -> "${diff / 86_400_000}d ago"
        else                        -> SimpleDateFormat("dd MMM", Locale.getDefault())
                                        .format(Date(timestamp))
    }
}