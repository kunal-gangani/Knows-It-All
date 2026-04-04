package com.example.know_it_all.presentation.ui.screen.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.navigation.Screen
import com.example.know_it_all.presentation.viewmodel.AuthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.VisualTransformation

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            "KnowItAll",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue
                emailError = getEmailError(newValue) ?: ""
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading,
            isError = emailError.isNotEmpty(),
            singleLine = true
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

        OutlinedTextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
                passwordError = getPasswordError(newValue) ?: ""
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading,
            isError = passwordError.isNotEmpty(),
            singleLine = true
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

        authState.error?.let {
            Text(
                "Login failed: $it",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val isFormValid = email.isNotEmpty() && password.isNotEmpty() && 
                        emailError.isEmpty() && passwordError.isEmpty()

        Button(
            onClick = {
                emailError = getEmailError(email) ?: ""
                passwordError = getPasswordError(password) ?: ""
                if (isFormValid) {
                    authViewModel.login(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !authState.isLoading && isFormValid
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate(Screen.Register.route) },
            enabled = !authState.isLoading
        ) {
            Text("Don't have an account? Register")
        }
    }
}
