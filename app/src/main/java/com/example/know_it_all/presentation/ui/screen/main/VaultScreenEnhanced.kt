package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.TokenBalanceCard

@Composable
fun VaultScreen(navController: NavHostController) {
    val tokenBalance by remember { mutableStateOf(450L) }
    
    val ledgerEntries = listOf(
        Triple("Swap with Alice", "5 stars", "2024-02-28"),
        Triple("Token earned", "+50 tokens", "2024-02-25"),
        Triple("Swap with Bob", "4 stars", "2024-02-20"),
        Triple("Token spent", "-30 tokens", "2024-02-15")
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("The Vault") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TokenBalanceCard(tokenBalance)

            Text(
                "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(ledgerEntries) { (description, amount, date) ->
                    LedgerEntryItem(description, amount, date)
                }
            }
        }
    }
}

@Composable
fun LedgerEntryItem(description: String, amount: String, date: String) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
