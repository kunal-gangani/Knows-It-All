package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
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
import com.example.know_it_all.KnowItAllApplication
import com.example.know_it_all.presentation.ui.components.TokenBalanceCard
import com.example.know_it_all.presentation.viewmodel.LedgerViewModel
import com.example.know_it_all.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreenEnhanced(
    navController: NavHostController,
    ledgerViewModel: LedgerViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val app = context.applicationContext as KnowItAllApplication
    
    val ledgerState by ledgerViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val userId = app.sessionManager.getUserId()
    val userEmail = app.sessionManager.getUserEmail()

    LaunchedEffect(authState.token, authState.userId) {
        if (authState.token != null && authState.userId != null) {
            ledgerViewModel.loadLedger(authState.token!!, authState.userId!!)
        }
    }

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
            TokenBalanceCard(ledgerState.tokenBalance)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    // TODO: Implement passport generation
                    // ledgerViewModel.generateSkillPassport(context, userProfile)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = !ledgerState.isLoading
            ) {
                Text("Generate Skill Passport (PDF)")
            }

            if (ledgerState.passportFile != null) {
                Text(
                    "Passport generated: ${ledgerState.passportFile?.name}",
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

            if (ledgerState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (ledgerState.error != null) {
                Text(
                    ledgerState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(ledgerState.ledgerEntries) { entry ->
                        LedgerEntryItem(
                            description = entry.transactionData,        
                            amount = "${entry.ratingGiven}★",          
                            date = entry.createdAt.toString()
                        )
                    }
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
