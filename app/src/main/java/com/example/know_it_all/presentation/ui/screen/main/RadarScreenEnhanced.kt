package com.example.know_it_all.presentation.ui.screen.main

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.BottomNavigationBar
import com.example.know_it_all.presentation.ui.components.UserCard
import com.example.know_it_all.presentation.viewmodel.AuthViewModel
import com.example.know_it_all.presentation.viewmodel.RadarViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Haversine formula — calculates real distance between two GPS points in km
private fun calculateDistanceKm(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RadarScreenEnhanced(
    navController: NavHostController,
    radarViewModel: RadarViewModel,
    authViewModel: AuthViewModel
) {
    val radarState by radarViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status.isGranted, authState.token) {
        if (locationPermissionState.status.isGranted && authState.token != null) {
            radarViewModel.loadNearbyUsers(authState.token!!, radarState.radiusKm)
        }
    }

    Scaffold(
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
                            "🧭 Skill Radar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Discover nearby mentors",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(
                        onClick = {
                            authState.token?.let {
                                radarViewModel.loadNearbyUsers(it, radarState.radiusKm)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (!locationPermissionState.status.isGranted) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📍", style = MaterialTheme.typography.headlineLarge)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Enable Location",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "We need your location to find nearby mentors and skills.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { locationPermissionState.launchPermissionRequest() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(48.dp)
                    ) {
                        Text("Grant Permission")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    item {
                        // Stats header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    "Search Radius: ${radarState.radiusKm}km",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${radarState.nearbyUsers.size} people found",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    when {
                        radarState.isLoading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        radarState.error != null -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.errorContainer,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            "Error",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            radarState.error!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = {
                                                authState.token?.let {
                                                    radarViewModel.loadNearbyUsers(it, radarState.radiusKm)
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                        }

                        radarState.nearbyUsers.isEmpty() -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Text("🔍", style = MaterialTheme.typography.displayLarge)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "No mentors found nearby",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Try increasing the radius or check back later",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            items(radarState.nearbyUsers) { user ->
                                val distanceText = if (
                                    radarState.currentLat != null && radarState.currentLon != null
                                ) {
                                    val km = calculateDistanceKm(
                                        radarState.currentLat!!, radarState.currentLon!!,
                                        user.latitude, user.longitude
                                    )
                                    "${String.format("%.1f", km)} km"
                                } else {
                                    "Unknown"
                                }

                                UserCard(
                                    name = user.name,
                                    distance = distanceText,
                                    trustScore = user.trustScore,
                                    onClick = {
                                        // TODO: navigate to user profile
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}