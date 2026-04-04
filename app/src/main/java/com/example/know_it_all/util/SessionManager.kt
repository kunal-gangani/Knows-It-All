package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.BottomNavigationBar
import com.example.know_it_all.presentation.ui.components.TokenBalanceCard
import com.example.know_it_all.presentation.viewmodel.AuthViewModel
import com.example.know_it_all.presentation.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreenEnhanced(
    navController: NavHostController,
    ledgerViewModel: LedgerViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val ledgerState by ledgerViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.token, authState.userId) {
        if (authState.token != null && authState.userId != null) {
            ledgerViewModel.loadLedger(authState.token!!, authState.userId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("The Vault") })
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
            TokenBalanceCard(ledgerState.tokenBalance)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // TODO: implement passport generation without User object
                    // needs ledgerViewModel.generateSkillPassport() to accept userId instead
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = !ledgerState.isLoading
            ) {
                Text("Generate Skill Passport (PDF)")
            }

            ledgerState.passportFile?.let {
                Text(
                    "Passport generated: ${it.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Text(
                "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )

            when {
                ledgerState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                ledgerState.error != null -> {
                    Text(
                        ledgerState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                ledgerState.ledgerEntries.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No transactions yet",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(ledgerState.ledgerEntries) { entry ->
                            LedgerEntryItem(
                                description = entry.transactionDescription,
                                amount = if (entry.transactionType == "EARNED")
                                    "+${entry.tokenAmount}"
                                else
                                    "-${entry.tokenAmount}",
                                date = entry.createdAt.toString()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LedgerEntryItem(description: String, amount: String, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // ✅ FIXED: was fillMaxSize
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), // ✅ FIXED: was fillMaxSize
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = amount,
                style = MaterialTheme.typography.titleSmall,
                color = if (amount.startsWith("+")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}