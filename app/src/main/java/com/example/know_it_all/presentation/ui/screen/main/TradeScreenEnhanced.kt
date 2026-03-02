package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.SwapStatusBadge
import com.example.know_it_all.presentation.ui.components.TokenBalanceCard

@Composable
fun TradeScreen(navController: NavHostController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Active", "History")
    
    val activeSwaps = listOf(
        Triple("Alice Johnson", "Python Exchange", "ACTIVE"),
        Triple("Bob Smith", "Web Dev Mentor", "ACTIVE")
    )
    
    val swapHistory = listOf(
        Triple("Charlie Davis", "Data Science", "COMPLETED"),
        Triple("Diana Prince", "Mobile App Dev", "COMPLETED"),
        Triple("Eve Wilson", "Cloud Computing", "CANCELLED")
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trade Center") })
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

            when (selectedTabIndex) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                "Active Swaps",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(activeSwaps) { (mentor, skill, status) ->
                            SwapItem(mentor, skill, status)
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                "Swap History",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(swapHistory) { (mentor, skill, status) ->
                            SwapItem(mentor, skill, status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwapItem(mentor: String, skill: String, status: String) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
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
