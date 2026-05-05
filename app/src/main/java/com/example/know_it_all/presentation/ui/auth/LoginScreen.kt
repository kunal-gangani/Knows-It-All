package com.example.know_it_all.presentation.ui.auth

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.know_it_all.data.repository.AuthRepository

/**
 * LoginScreen
 *
 * Premium Android UI with:
 *   - Dark mode theme (#0F0F0F, #1A1A1A)
 *   - High-contrast typography
 *   - Subtle glassmorphism effect
 *   - Neon green (#33FF33) border glow on inputs
 *   - Clean, minimalist design
 *
 * Args:
 *   - onLoginSuccess: Callback triggered after successful login
 *   - authRepository: Injected repository for login logic
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authRepository: AuthRepository? = null,
    viewModel: LoginViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository ?: throw IllegalStateException("AuthRepository must be provided")) as T
        }
    })
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    val passwordFocusRequester = FocusRequester()

    // Trigger navigation when login succeeds
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            viewModel.resetLoginSuccess()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ✅ Header: "KnowsItAll"
            Text(
                text = "KnowsItAll",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF33FF33),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Decrypt & Enter",
                fontSize = 14.sp,
                color = Color(0xFFCCCCCC),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // ✅ Login Card with subtle glassmorphism
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                color = Color(0xFF1A1A1A),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Error message
                    val errorMsg = uiState.errorMessage
                    if (errorMsg != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            color = Color(0xFF4D1F1F),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = errorMsg,
                                color = Color(0xFFFF6B6B),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("Identity (Email)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { passwordFocusRequester.requestFocus() }
                        ),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF33FF33),
                            unfocusedBorderColor = Color(0xFF444444),
                            focusedLabelColor = Color(0xFF33FF33),
                            unfocusedLabelColor = Color(0xFF888888),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { Text("Access Key (Password)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequester)
                            .padding(bottom = 24.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.login() }
                        ),
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val icon = if (showPassword) "🔓" else "🔒"
                            Text(
                                text = icon,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable { showPassword = !showPassword }
                            )
                        },
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF33FF33),
                            unfocusedBorderColor = Color(0xFF444444),
                            focusedLabelColor = Color(0xFF33FF33),
                            unfocusedLabelColor = Color(0xFF888888),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Login Button
                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF33FF33),
                            disabledContainerColor = Color(0xFF1A4D1A),
                            contentColor = Color(0xFF0F0F0F),
                            disabledContentColor = Color(0xFF666666)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF0F0F0F),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Decrypt & Enter",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // ✅ Footer: Security badge
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "🔐 Secured by Qubrix",
                fontSize = 11.sp,
                color = Color(0xFF666666),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
