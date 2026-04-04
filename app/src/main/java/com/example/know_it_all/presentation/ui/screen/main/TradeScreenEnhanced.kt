package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.BottomNavigationBar
import com.example.know_it_all.presentation.ui.components.SwapStatusBadge
import com.example.know_it_all.presentation.viewmodel.AuthViewModel
import com.example.know_it_all.presentation.viewmodel.TradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreenEnhanced(
    navController: NavHostController,
    tradeViewModel: TradeViewModel,
    authViewModel: AuthViewModel
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Active", "History")

    val tradeState by tradeViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(selectedTabIndex, authState.token) {
        authState.token?.let { token ->
            if (selectedTabIndex == 0) {
                tradeViewModel.loadActiveSwaps(token)
            } else {
                tradeViewModel.loadSwapHistory(token)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trade Center") })
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = navController.currentBackStackEntry?.destination?.route
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            if (tradeState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val swaps = if (selectedTabIndex == 0) {
                    tradeState.activeSwaps
                } else {
                    tradeState.swapHistory
                }

                if (swaps.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (selectedTabIndex == 0) "No active swaps"
                            else "No swap history yet",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(swaps) { swap ->
                            SwapItem(
                                mentor = swap.mentorName,
                                skill = swap.skillName,
                                status = swap.status
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwapItem(mentor: String, skill: String, status: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth() 
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = mentor,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = skill,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            SwapStatusBadge(status, modifier = Modifier.padding(top = 8.dp))
        }
    }
}