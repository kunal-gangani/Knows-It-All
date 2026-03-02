package com.example.know_it_all.presentation.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.SkillBadge

@Composable
fun SkillProfileScreen(navController: NavHostController) {
    val skillsLearned = remember {
        listOf("Python", "Web Development", "Data Science", "UI/UX Design")
    }
    
    val skillsTaught = remember {
        listOf("Java", "Android Development", "Mobile Apps")
    }
    
    var isGeneratingPDF by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Skill Passport") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Profile Header
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "John Developer",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "Member since Feb 2024",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "⭐ 4.8 / 5.0 Trust Score",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            // Skills Learned
            Text(
                "Skills Learned (${skillsLearned.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skillsLearned.onEach { skill ->
                    SkillBadge(skill)
                }
            }

            // Skills Taught
            Text(
                "Skills Taught (${skillsTaught.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skillsTaught.onEach { skill ->
                    SkillBadge(skill)
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isGeneratingPDF = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isGeneratingPDF) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp)
                            )
                            Text("Generating PDF...")
                        }
                    } else {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Text("Download Skill Passport")
                    }
                }

                Button(
                    onClick = { /* TODO: Implement video call */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Video Call")
                    Text("Schedule Video Session")
                }
            }
        }
    }
}
