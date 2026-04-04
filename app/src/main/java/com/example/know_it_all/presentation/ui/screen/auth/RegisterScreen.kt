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
import androidx.compose.material.icons.filled.Person
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

private fun getNameError(name: String): String? = when {
    name.isBlank() -> "Full name is required"
    name.length < 2 -> "Name must be at least 2 characters"
    else -> null
}

private fun getEmailError(email: String): String? = when {
    email.isBlank() -> "Email is required"
    !isValidEmail(email) -> "Invalid email format"
    else -> null
}

private fun getPasswordError(password: String): String? = when {
    password.isBlank() -> "Password is required"
    password.length < 6 -> "Password must be at least 6 characters"
    password.length > 128 -> "Password is too long"
    else -> null
}

private fun getPasswordMatchError(password: String, confirmPassword: String): String? = when {
    confirmPassword.isBlank() -> "Please confirm your password"
    password != confirmPassword -> "Passwords do not match"
    else -> null
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var passwordMatchError by remember { mutableStateOf("") }
    
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(Screen.Radar.route) {
                popUpTo(0) { inclusive = true } 
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
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "✨",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Join KnowItAll",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Start sharing your skills today",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            // Full Name field
            Text(
                "Full Name",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    name = newValue
                    nameError = getNameError(newValue) ?: ""
                },
                placeholder = { Text("John Doe") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !authState.isLoading,
                isError = nameError.isNotEmpty(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            if (nameError.isNotEmpty()) {
                Text(
                    nameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
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
                    passwordMatchError = getPasswordMatchError(newValue, confirmPassword) ?: ""
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
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
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

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            Text(
                "Confirm Password",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { newValue ->
                    confirmPassword = newValue
                    passwordMatchError = getPasswordMatchError(password, newValue) ?: ""
                },
                placeholder = { Text("••••••") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !authState.isLoading,
                isError = passwordMatchError.isNotEmpty(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            if (passwordMatchError.isNotEmpty()) {
                Text(
                    passwordMatchError,
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
                        "Registration Failed",
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

            // Register button
            val isFormValid = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                            nameError.isEmpty() && emailError.isEmpty() && 
                            passwordError.isEmpty() && passwordMatchError.isEmpty()

            ModernButton(
                text = "Create Account",
                onClick = {
                    nameError = getNameError(name) ?: ""
                    emailError = getEmailError(email) ?: ""
                    passwordError = getPasswordError(password) ?: ""
                    passwordMatchError = getPasswordMatchError(password, confirmPassword) ?: ""
                    if (isFormValid) {
                        authViewModel.register(name, email, password)
                    }
                },
                enabled = isFormValid,
                isLoading = authState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Login link
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Already have an account? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                TextButton(
                    onClick = { navController.popBackStack() },
                    enabled = !authState.isLoading,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Sign in here",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
