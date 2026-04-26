package com.example.know_it_all.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.know_it_all.KnowItAllApplication
import com.example.know_it_all.presentation.ui.screen.auth.LoginScreen
import com.example.know_it_all.presentation.ui.screen.auth.RegisterScreen
import com.example.know_it_all.presentation.ui.screen.auth.SplashScreen
import com.example.know_it_all.presentation.ui.screen.main.RadarScreenEnhanced
import com.example.know_it_all.presentation.ui.screen.main.SkillProfileScreenEnhanced
import com.example.know_it_all.presentation.ui.screen.main.TradeScreenEnhanced
import com.example.know_it_all.presentation.ui.screen.main.VaultScreenEnhanced
import com.example.know_it_all.presentation.viewmodel.AuthViewModel
import com.example.know_it_all.presentation.viewmodel.LedgerViewModel
import com.example.know_it_all.presentation.viewmodel.RadarViewModel
import com.example.know_it_all.presentation.viewmodel.SkillViewModel
import com.example.know_it_all.presentation.viewmodel.TradeViewModel
import com.example.know_it_all.presentation.viewmodel.ViewModelFactory

// ---------------------------------------------------------------------------
// Route definitions — single source of truth for all navigation strings
// ---------------------------------------------------------------------------
sealed class Screen(val route: String) {
    object Splash       : Screen("splash")
    object Login        : Screen("login")
    object Register     : Screen("register")
    object Radar        : Screen("radar")
    object Trade        : Screen("trade")
    object Vault        : Screen("vault")
    object SkillProfile : Screen("skill_profile")
}

// ---------------------------------------------------------------------------
// Root navigation host
// ---------------------------------------------------------------------------

/**
 * Architecture decisions:
 *
 *  1. AuthViewModel is the ONLY ViewModel at NavHost scope — it must outlive
 *     all destinations so auth state (userId, isAuthenticated) is stable
 *     across navigation events.
 *
 *  2. All feature ViewModels are scoped to their NavBackStackEntry — created
 *     when navigating to a screen and destroyed when leaving it. This prevents
 *     all 5 ViewModels from being held in memory simultaneously.
 *
 *  3. Feature screens receive only primitives (userId, userName) from authState
 *     — never the full AuthViewModel. This breaks coupling between auth logic
 *     and feature screens and makes each screen independently testable.
 *
 *  4. onLogout is a lambda passed down from AuthViewModel — screens can trigger
 *     logout without holding a reference to AuthViewModel.
 */
@Composable
fun KnowItAllNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    val context = LocalContext.current
    val app = context.applicationContext as KnowItAllApplication

    // AuthViewModel — NavHost scope (survives all navigation)
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory(
            userRepository = app.userRepository,
            sessionManager = app.sessionManager
        )
    )
    val authState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // ── Splash ──────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                isLoggedIn = authState.isAuthenticated
            )
        }

        // ── Login ────────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // ── Register ─────────────────────────────────────────────────────────
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // ── Radar ────────────────────────────────────────────────────────────
        composable(Screen.Radar.route) { backStackEntry ->
            val radarViewModel: RadarViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = ViewModelFactory(
                    userRepository = app.userRepository,
                    sessionManager = app.sessionManager
                )
            )
            RadarScreenEnhanced(
                navController = navController,
                radarViewModel = radarViewModel,
                userId = authState.userId ?: "",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Trade ────────────────────────────────────────────────────────────
        composable(Screen.Trade.route) { backStackEntry ->
            val tradeViewModel: TradeViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = ViewModelFactory(
                    swapRepository = app.swapRepository,
                    sessionManager = app.sessionManager
                )
            )
            TradeScreenEnhanced(
                navController = navController,
                tradeViewModel = tradeViewModel,
                userId = authState.userId ?: "",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Vault ────────────────────────────────────────────────────────────
        composable(Screen.Vault.route) { backStackEntry ->
            val ledgerViewModel: LedgerViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = ViewModelFactory(
                    ledgerRepository = app.ledgerRepository,
                    skillRepository = app.skillRepository,
                    userRepository = app.userRepository,
                    sessionManager = app.sessionManager
                )
            )
            VaultScreenEnhanced(
                navController = navController,
                ledgerViewModel = ledgerViewModel,
                userId = authState.userId ?: "",
                userName = authState.userName ?: "",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Skill Profile ─────────────────────────────────────────────────────
        composable(Screen.SkillProfile.route) { backStackEntry ->
            val skillViewModel: SkillViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = ViewModelFactory(
                    skillRepository = app.skillRepository,
                    sessionManager = app.sessionManager
                )
            )
            SkillProfileScreenEnhanced(
                navController = navController,
                skillViewModel = skillViewModel,
                userId = authState.userId ?: "",
                userName = authState.userName ?: "",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}