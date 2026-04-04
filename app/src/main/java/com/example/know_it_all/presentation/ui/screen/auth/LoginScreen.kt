package com.example.know_it_all.presentation.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.components.ModernButton
import com.example.know_it_all.presentation.ui.navigation.Screen
import com.example.know_it_all.presentation.viewmodel.AuthViewModel

// Validation helper functions
private fun isValidEmail(email: String): Boolean {
    val emailRegex = """^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$""".toRegex()
    return email.matches(emailRegex)
}

private fun getEmailError(email: String): String? = when {
    email.isBlank() -> "Email is required"
    !isValidEmail(email) -> "Invalid email format"
    else -> null
}

private fun getPasswordError(password: String): String? = when {
    password.isBlank() -> "Password is required"
    password.length < 6 -> "Password must be at least 6 characters"
    else -> null
}

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(Screen.Radar.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.foundation.background.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🎓",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "KnowItAll",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Share Skills. Learn Together.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Text(
                "Welcome Back",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email field
            Text(
                "Email Address",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue
                    emailError = getEmailError(newValue) ?: ""
                },
                placeholder = { Text("your@email.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !authState.isLoading,
                isError = emailError.isNotEmpty(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            if (emailError.isNotEmpty()) {
                Text(
                    emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            Text(
                "Password",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { newValue ->
                    password = newValue
                    passwordError = getPasswordError(newValue) ?: ""
                },
                placeholder = { Text("••••••") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !authState.isLoading,
                isError = passwordError.isNotEmpty(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            if (passwordError.isNotEmpty()) {
                Text(
                    passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            authState.error?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        "Login Failed",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Login button
            val isFormValid = email.isNotEmpty() && password.isNotEmpty() && 
                            emailError.isEmpty() && passwordError.isEmpty()

            ModernButton(
                text = "Sign In",
                onClick = {
                    emailError = getEmailError(email) ?: ""
                    passwordError = getPasswordError(password) ?: ""
                    if (isFormValid) {
                        authViewModel.login(email, password)
                    }
                },
                enabled = isFormValid,
                isLoading = authState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Register link
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Don't have an account? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                TextButton(
                    onClick = { navController.navigate(Screen.Register.route) },
                    enabled = !authState.isLoading,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Create an account",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
