package com.example.know_it_all.presentation.ui.screen.main

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.know_it_all.data.model.User
import com.example.know_it_all.presentation.ui.components.BottomNavigationBar
import com.example.know_it_all.presentation.ui.theme.KnowItAllColors
import com.example.know_it_all.presentation.viewmodel.RadarViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Fixes applied:
 *  1. AuthViewModel removed — screen receives userId: String directly.
 *  2. Token no longer read from UI layer — ViewModel reads it internally.
 *  3. loadNearbyUsers() called with no params (token is internal).
 *  4. radarState.currentLat/Lon are now non-nullable Double (fixed ViewModel).
 *  5. LaunchedEffect no longer references authState.token.
 *  6. nearbyUsers is now List<User> (Room entity) not List<UserDTO>.
 *
 * Design: cream background, photo-forward profile cards with overlaid
 * skill chip tags, acid-green "Connect" CTA, pulsing online presence dot.
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RadarScreenEnhanced(
    navController: NavHostController,
    radarViewModel: RadarViewModel,
    userId: String,
    onLogout: () -> Unit = {}
) {
    val radarState by radarViewModel.uiState.collectAsState()

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Request permission on first launch, then load users once granted
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            radarViewModel.loadNearbyUsers()   // ✅ no token param — read internally
        }
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
                            text = "SKILL RADAR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                            color = KnowItAllColors.WarmGray
                        )
                        Text(
                            text = "Nearby mentors",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = KnowItAllColors.NearBlack,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KnowItAllColors.Cream
                ),
                actions = {
                    // Online filter toggle
                    IconButton(onClick = { radarViewModel.toggleOnlineFilter() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (radarState.onlineOnly) KnowItAllColors.AcidGreen
                                    else KnowItAllColors.CreamDark,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (radarState.onlineOnly) KnowItAllColors.NearBlack
                                        else KnowItAllColors.WarmGray,
                                        CircleShape
                                    )
                            )
                        }
                    }
                    IconButton(onClick = { radarViewModel.loadNearbyUsers() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = KnowItAllColors.NearBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KnowItAllColors.Cream)
                .padding(innerPadding)
        ) {
            if (!locationPermissionState.status.isGranted) {
                // Permission prompt
                LocationPermissionPrompt(
                    onRequest = { locationPermissionState.launchPermissionRequest() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    // Stats strip
                    item {
                        RadarStatsStrip(
                            count = radarState.nearbyUsers.size,
                            radiusKm = radarState.radiusKm,
                            isLoading = radarState.isLoading
                        )
                    }

                    // Error state
                    if (radarState.error != null) {
                        item {
                            ErrorCard(
                                message = radarState.error!!,
                                onRetry = {
                                    radarViewModel.clearError()
                                    radarViewModel.loadNearbyUsers()
                                }
                            )
                        }
                    }

                    // Empty state
                    if (!radarState.isLoading && radarState.nearbyUsers.isEmpty()
                        && radarState.error == null) {
                        item { RadarEmptyState() }
                    }

                    // User cards — staggered fade-in
                    itemsIndexed(
                        radarState.nearbyUsers,
                        key = { _, user -> user.uid }          // ✅ stable keys for animation
                    ) { index, user ->
                        val distance = calculateDistanceKm(
                            radarState.currentLat, radarState.currentLon,
                            user.latitude, user.longitude
                        )
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300, delayMillis = index * 60)) +
                                    slideInVertically(tween(300, delayMillis = index * 60)) { it / 4 }
                        ) {
                            MentorProfileCard(
                                user = user,
                                distanceKm = distance,
                                onConnect = { radarViewModel.selectUser(user) }
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
// Mentor profile card — photo-forward, matches Dribbble reference
// ---------------------------------------------------------------------------

@Composable
private fun MentorProfileCard(
    user: User,
    distanceKm: Double,
    onConnect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(KnowItAllColors.NearBlack)
            .clickable(onClick = onConnect)
    ) {
        // Placeholder avatar background (replace with AsyncImage + Coil in production)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KnowItAllColors.CharcoalGray,
                            KnowItAllColors.NearBlack
                        )
                    )
                )
        )

        // Bottom gradient scrim for text legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KnowItAllColors.NearBlack.copy(alpha = 0f),
                            KnowItAllColors.NearBlack.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        // Skill tag chips — top left (Photography, Indie Rock style from reference)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Verified chip
            if (user.profileVerified) {
                SkillChip(label = "✓ Verified", isAccent = true)
            }
            // Trust score chip
            if (user.trustScore >= 4.0f) {
                SkillChip(label = "⭐ ${String.format("%.1f", user.trustScore)}")
            }
        }

        // Online presence dot — top right (pulsing green dot from reference)
        if (user.isOnline) {
            PulsingOnlineDot(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
            )
        }

        // Bottom content — name, role, distance, CTA
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = user.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = KnowItAllColors.Cream,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${user.skillTokenBalance} tokens · ${String.format("%.1f", distanceKm)} km away",
                fontSize = 12.sp,
                color = KnowItAllColors.Cream.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Connect + Next row — matches "< Next | Connect" from reference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "About" chip
                Box(
                    modifier = Modifier
                        .background(
                            KnowItAllColors.Cream.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "About",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = KnowItAllColors.Cream
                    )
                }

                // Connect — acid green CTA
                Button(
                    onClick = onConnect,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KnowItAllColors.AcidGreen,
                        contentColor = KnowItAllColors.NearBlack
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Connect",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Supporting composables
// ---------------------------------------------------------------------------

@Composable
private fun PulsingOnlineDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )
    Box(modifier = modifier.size(14.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(14.dp)
                .background(KnowItAllColors.AcidGreen.copy(alpha = 0.3f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(KnowItAllColors.AcidGreen, CircleShape)
        )
    }
}

@Composable
private fun SkillChip(label: String, isAccent: Boolean = false) {
    Box(
        modifier = Modifier
            .background(
                if (isAccent) KnowItAllColors.AcidGreen
                else KnowItAllColors.Cream.copy(alpha = 0.18f),
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isAccent) KnowItAllColors.AcidGreen
                else KnowItAllColors.Cream.copy(alpha = 0.25f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isAccent) KnowItAllColors.NearBlack else KnowItAllColors.Cream
        )
    }
}

@Composable
private fun RadarStatsStrip(count: Int, radiusKm: Double, isLoading: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KnowItAllColors.CreamDark, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (isLoading) "Scanning..." else "$count people found",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = KnowItAllColors.NearBlack
            )
            Text(
                text = "Within ${radiusKm}km radius",
                fontSize = 12.sp,
                color = KnowItAllColors.CharcoalGray
            )
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    if (isLoading) KnowItAllColors.Ochre else KnowItAllColors.AcidGreen,
                    CircleShape
                )
        )
    }
}

@Composable
private fun LocationPermissionPrompt(onRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📍", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Enable Location",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = KnowItAllColors.NearBlack,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We need your location to find\nnearby mentors and skills.",
            fontSize = 14.sp,
            color = KnowItAllColors.CharcoalGray,
            lineHeight = 21.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRequest,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KnowItAllColors.AcidGreen,
                contentColor = KnowItAllColors.NearBlack
            )
        ) {
            Text("Grant Permission", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RadarEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔍", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No mentors nearby",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = KnowItAllColors.NearBlack
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Try increasing the radius\nor check back later",
            fontSize = 13.sp,
            color = KnowItAllColors.CharcoalGray,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(KnowItAllColors.ErrorContainer, RoundedCornerShape(14.dp))
            .border(1.dp, KnowItAllColors.ErrorRed.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Couldn't load mentors",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = KnowItAllColors.ErrorRed
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(message, fontSize = 12.sp, color = KnowItAllColors.CharcoalGray)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KnowItAllColors.NearBlack,
                contentColor = KnowItAllColors.Cream
            )
        ) {
            Text("Retry", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun calculateDistanceKm(
    lat1: Double, lon1: Double, lat2: Double, lon2: Double
): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}