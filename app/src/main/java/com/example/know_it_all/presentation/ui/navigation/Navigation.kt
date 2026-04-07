package com.example.know_it_all.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.know_it_all.presentation.ui.screen.main.TradeScreenEnhanced
import com.example.know_it_all.presentation.ui.screen.main.VaultScreenEnhanced
import com.example.know_it_all.presentation.ui.screen.main.SkillProfileScreenEnhanced
import com.example.know_it_all.presentation.viewmodel.AuthViewModel
import com.example.know_it_all.presentation.viewmodel.RadarViewModel
import com.example.know_it_all.presentation.viewmodel.TradeViewModel
import com.example.know_it_all.presentation.viewmodel.LedgerViewModel
import com.example.know_it_all.presentation.viewmodel.SkillViewModel
import com.example.know_it_all.presentation.viewmodel.ViewModelFactory

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Radar : Screen("radar")
    object Trade : Screen("trade")
    object Vault : Screen("vault")
    object SkillProfile : Screen("skill_profile")
}

@Composable
fun KnowItAllNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    val context = LocalContext.current
    val app = context.applicationContext as KnowItAllApplication
    
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory(
            userRepository = app.userRepository,
            sessionManager = app.sessionManager
        )
    )
    
    val radarViewModel: RadarViewModel = viewModel(
        factory = ViewModelFactory(
            userRepository = app.userRepository
        )
    )
    
    val tradeViewModel: TradeViewModel = viewModel(
        factory = ViewModelFactory(
            userRepository = app.userRepository,
            swapRepository = app.swapRepository
        )
    )
    
    val ledgerViewModel: LedgerViewModel = viewModel(
        factory = ViewModelFactory(
            userRepository = app.userRepository,
            ledgerRepository = app.ledgerRepository,
            skillRepository = app.skillRepository
        )
    )
    
    val skillViewModel: SkillViewModel = viewModel(
        factory = ViewModelFactory(
            userRepository = app.userRepository,
            skillRepository = app.skillRepository
        )
    )
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                isLoggedIn = app.sessionManager.isLoggedIn()
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Screen.Radar.route) {
            RadarScreenEnhanced(navController, radarViewModel, authViewModel)
        }
        composable(Screen.Trade.route) {
            TradeScreenEnhanced(navController, tradeViewModel, authViewModel)
        }
        composable(Screen.Vault.route) {
            VaultScreenEnhanced(navController, ledgerViewModel, authViewModel)
        }
        composable(Screen.SkillProfile.route) {
            SkillProfileScreenEnhanced(navController, skillViewModel, authViewModel)
        }
    }
}
