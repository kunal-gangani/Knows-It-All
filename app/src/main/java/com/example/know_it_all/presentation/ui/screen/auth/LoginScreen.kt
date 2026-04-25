package com.example.know_it_all.presentation.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.navigation.Screen
import com.example.know_it_all.presentation.ui.theme.KnowItAllColors
import com.example.know_it_all.presentation.viewmodel.AuthViewModel

private fun isValidEmail(email: String) =
    """^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$""".toRegex().matches(email)

private fun emailError(email: String) = when {
    email.isBlank() -> "Email is required"
    !isValidEmail(email) -> "Invalid email format"
    else -> null
}

private fun passwordError(password: String) = when {
    password.isBlank() -> "Password is required"
    password.length < 6 -> "At least 6 characters"
    else -> null
}

/**
 * Fixes applied:
 *  1. No longer accepts AuthViewModel directly — NavGraph fix removed it.
 *     Kept for now to match current NavGraph; will be fully removed when
 *     NavGraph is updated to pass only primitives.
 *  2. authState.token no longer referenced (removed from AuthUiState).
 *  3. clearError() called after navigation so stale errors don't persist.
 *  4. Keyboard ImeAction wired so "Next" moves focus and "Done" submits.
 *
 * Design: editorial cream background, near-black type, acid green CTA,
 * chip-style skill tags, minimal border-only inputs.
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            authViewModel.clearError()
            navController.navigate(Screen.Radar.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    val emailErr = if (showErrors) emailError(email) else null
    val passErr = if (showErrors) passwordError(password) else null
    val isFormValid = emailError(email) == null && passwordError(password) == null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KnowItAllColors.Cream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // Logo mark
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(KnowItAllColors.NearBlack, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("K", fontSize = 28.sp, fontWeight = FontWeight.Black,
                    color = KnowItAllColors.AcidGreen)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Header
            Text(
                text = "Welcome\nback.",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = KnowItAllColors.NearBlack,
                lineHeight = 46.sp,
                letterSpacing = (-1.5).sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sign in to continue trading skills.",
                fontSize = 15.sp,
                color = KnowItAllColors.CharcoalGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email input
            FieldLabel("Email address")
            KnowItAllTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "your@email.com",
                error = emailErr,
                enabled = !authState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password input
            FieldLabel("Password")
            KnowItAllTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "••••••",
                error = passErr,
                enabled = !authState.isLoading,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = KnowItAllColors.WarmGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        showErrors = true
                        if (isFormValid) authViewModel.login(email, password)
                    }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // API error
            AnimatedVisibility(
                visible = authState.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                authState.error?.let { err ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                KnowItAllColors.ErrorContainer,
                                RoundedCornerShape(10.dp)
                            )
                            .border(1.dp, KnowItAllColors.ErrorRed.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(err, fontSize = 13.sp, color = KnowItAllColors.ErrorRed)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // CTA — acid green, full width, near-black text
            Button(
                onClick = {
                    showErrors = true
                    if (isFormValid) authViewModel.login(email, password)
                },
                enabled = !authState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KnowItAllColors.AcidGreen,
                    contentColor = KnowItAllColors.NearBlack,
                    disabledContainerColor = KnowItAllColors.AcidGreen.copy(alpha = 0.4f),
                    disabledContentColor = KnowItAllColors.NearBlack.copy(alpha = 0.4f)
                )
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = KnowItAllColors.NearBlack,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Register link
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't have an account?",
                    fontSize = 13.sp,
                    color = KnowItAllColors.CharcoalGray
                )
                TextButton(
                    onClick = { navController.navigate(Screen.Register.route) },
                    enabled = !authState.isLoading
                ) {
                    Text(
                        "Create one",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KnowItAllColors.NearBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Shared input components (auth screens only — move to components/ if reused)
// ---------------------------------------------------------------------------

@Composable
internal fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = KnowItAllColors.CharcoalGray,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
internal fun KnowItAllTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, fontSize = 14.sp, color = KnowItAllColors.WarmGray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(KnowItAllColors.CreamDark, RoundedCornerShape(12.dp))
                .then(
                    if (error != null)
                        Modifier.border(1.5.dp, KnowItAllColors.ErrorRed, RoundedCornerShape(12.dp))
                    else
                        Modifier.border(1.dp, KnowItAllColors.CreamDeep, RoundedCornerShape(12.dp))
                ),
            enabled = enabled,
            singleLine = true,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = KnowItAllColors.CreamDark,
                unfocusedContainerColor = KnowItAllColors.CreamDark,
                disabledContainerColor = KnowItAllColors.CreamDeep,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = KnowItAllColors.NearBlack,
                unfocusedTextColor = KnowItAllColors.NearBlack,
                cursorColor = KnowItAllColors.NearBlack
            )
        )
        AnimatedVisibility(visible = error != null) {
            error?.let {
                Text(
                    text = it,
                    fontSize = 11.sp,
                    color = KnowItAllColors.ErrorRed,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }
    }
}