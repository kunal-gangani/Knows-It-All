package com.example.know_it_all.presentation.ui.screen.main

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
                title = { Text("Skill Radar") },
                actions = {
                    IconButton(onClick = {
                        authState.token?.let {
                            radarViewModel.loadNearbyUsers(it, radarState.radiusKm)
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (!locationPermissionState.status.isGranted) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(64.dp))
                    Text(
                        "Location permission is required to find nearby mentors.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            } else {
                Text(
                    "Mentors & learners within ${radarState.radiusKm}km",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when {
                    radarState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    radarState.error != null -> {
                        Text(
                            radarState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            authState.token?.let {
                                radarViewModel.loadNearbyUsers(it, radarState.radiusKm)
                            }
                        }) {
                            Text("Retry")
                        }
                    }

                    radarState.nearbyUsers.isEmpty() -> {
                        Text(
                            "No mentors found nearby. Try increasing the radius.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        LazyColumn {
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
                }
            }
        }
    }
}