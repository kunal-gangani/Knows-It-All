package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.UserCard

@Composable
fun RadarScreen(navController: NavHostController) {
    var isLoading by remember { mutableStateOf(false) }
    var nearbyUsers by remember { mutableStateOf(emptyList<Pair<String, Float>>()) }

    // Simulate loading nearby users
    if (nearbyUsers.isEmpty() && !isLoading) {
        isLoading = true
        nearbyUsers = listOf(
            Pair("Alice Johnson (2.5 km)", 4.8f),
            Pair("Bob Smith (3.2 km)", 4.5f),
            Pair("Charlie Davis (1.8 km)", 4.9f)
        )
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skill Radar") },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { }
            ) {
                Text("📍")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Find mentors & learners nearby (5km radius)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(nearbyUsers) { (name, trustScore) ->
                        UserCard(
                            name = name,
                            distance = name.substringAfter("("),
                            trustScore = trustScore,
                            onClick = {
                                // Navigate to user profile or initiate swap
                            }
                        )
                    }
                }
            }
        }
    }
}
