package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.KnowItAllApplication
import com.example.know_it_all.presentation.ui.components.BottomNavigationBar
import com.example.know_it_all.presentation.ui.components.SkillBadge
import com.example.know_it_all.presentation.viewmodel.AuthViewModel
import com.example.know_it_all.presentation.viewmodel.SkillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillProfileScreenEnhanced(
    navController: NavHostController,
    skillViewModel: SkillViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val app = context.applicationContext as KnowItAllApplication

    val skillState by skillViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    // ✅ FIXED: use SessionManager fields instead of getUser()
    val userName = app.sessionManager.getUserName() ?: "User Profile"
    val userEmail = app.sessionManager.getUserEmail() ?: ""

    LaunchedEffect(authState.token, authState.userId) {
        if (authState.token != null && authState.userId != null) {
            skillViewModel.loadUserSkills(authState.token!!, authState.userId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Skill Profile") })
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = navController.currentBackStackEntry?.destination?.route
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            userName,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Member since 2024",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Your Skill Profile",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    // TODO: Add QR code generation when library is available
                }
            }

            item {
                Text(
                    "Verified Skills (${skillState.userSkills.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (skillState.isLoading) {
                item { CircularProgressIndicator() }
            } else if (skillState.userSkills.isEmpty()) {
                item {
                    Text(
                        "No skills added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                items(skillState.userSkills) { skill ->
                    SkillBadge(
                        skillName = "${skill.skillName} · ${skill.proficiencyLevel.name}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}