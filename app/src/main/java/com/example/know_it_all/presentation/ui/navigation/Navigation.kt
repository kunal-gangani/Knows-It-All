package com.example.know_it_all.presentation.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.know_it_all.data.local.prefs.PreferenceManager
import com.example.know_it_all.data.repository.AuthRepository
import com.example.know_it_all.presentation.ui.auth.LoginScreen
import com.example.know_it_all.presentation.ui.screen.main.SkillProfileScreenEnhanced
import com.example.know_it_all.presentation.viewmodel.SkillViewModel

/**
 * KnowItAllNavigation
 *
 * Routes the app based on authentication state:
 *   - If no token in PreferenceManager → show Login screen
 *   - If valid token exists → show Main Hub (SkillProfileScreenEnhanced)
 *
 * Navigation Graph:
 *   - "login": LoginScreen composable
 *   - "main_hub": SkillProfileScreenEnhanced (main app)
 *
 * On successful login, the LoginScreen callback routes to main_hub and
 * pops the login screen from the back stack so the user can't navigate back.
 */
@Composable
fun KnowItAllNavigation(
    authRepository: AuthRepository
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }

    // Determine starting destination based on token
    val startDestination = if (prefManager.isLoggedIn()) "main_hub" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = {
                    navController.navigate("main_hub") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main_hub") {
            // Get user data from PreferenceManager
            val userId = prefManager.getUserId() ?: ""
            val userName = prefManager.getAuthToken() ?: "User"
            
            // Create SkillViewModel
            val skillViewModel: SkillViewModel = viewModel()

            SkillProfileScreenEnhanced(
                navController = navController,
                skillViewModel = skillViewModel,
                userId = userId,
                userName = userName,
                onLogout = {
                    // Clear session and navigate back to login
                    prefManager.clearSession()
                    navController.navigate("login") {
                        popUpTo("main_hub") { inclusive = true }
                    }
                }
            )
        }
    }
}
