package com.examencivique.ui.auth

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examencivique.R
import com.examencivique.data.repository.AuthRepository
import com.examencivique.data.repository.AuthResult
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue
import com.examencivique.ui.theme.FrenchRed
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepo: AuthRepository,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        // Hero icon — French flag colored circle
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(12.dp, CircleShape, spotColor = FrenchBlue.copy(alpha = 0.3f))
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(FrenchBlue, Color(0xFF4A6CF7), FrenchRed))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Flag, null, tint = Color.White, modifier = Modifier.size(36.dp))
        }

        Spacer(Modifier.height(20.dp))

        Text("Examen Civique", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = FrenchBlue)
        Spacer(Modifier.height(6.dp))
        Text(s.authLogin, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(36.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text(s.authEmail) },
            leadingIcon = { Icon(Icons.Filled.Email, null, tint = FrenchBlue) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(12.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text(s.authPassword) },
            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = FrenchBlue) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        // Forgot password
        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(s.authForgotPassword, fontSize = 13.sp, color = FrenchBlue)
        }

        // Error message
        if (errorMessage != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FrenchRed.copy(alpha = 0.08f))
            ) {
                Text(
                    errorMessage!!,
                    color = FrenchRed,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // Login button — Duolingo bouncy style
        BounceButton(
            onClick = {
                errorMessage = null
                isLoading = true
                scope.launch {
                    when (val result = authRepo.signInWithEmail(email.trim(), password)) {
                        is AuthResult.Success -> onLoginSuccess()
                        is AuthResult.Error -> errorMessage = s.authErrorMap(result.message)
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            containerColor = FrenchBlue,
            shadowColor = Color(0xFF001A6E)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(s.authLoginButton, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                s.authOr,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        }

        Spacer(Modifier.height(24.dp))

        // Google Sign-In — Duolingo bouncy style
        BounceButton(
            onClick = {
                errorMessage = null
                isLoading = true
                scope.launch {
                    when (val result = authRepo.signInWithGoogle(context)) {
                        is AuthResult.Success -> onLoginSuccess()
                        is AuthResult.Error -> {
                            val mapped = s.authErrorMap(result.message)
                            if (mapped.isNotEmpty()) errorMessage = mapped
                        }
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shadowColor = Color.Gray.copy(alpha = 0.3f),
            border = true
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.width(12.dp))
            Text(s.authGoogleSignIn, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }

        Spacer(Modifier.height(32.dp))

        // Navigate to register
        TextButton(onClick = onNavigateToRegister) {
            Text(s.authNoAccount, fontSize = 14.sp, color = FrenchBlue, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(32.dp))
    }
}

/**
 * Duolingo-style button: raised with bottom shadow, bouncy press animation.
 */
@Composable
fun BounceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = FrenchBlue,
    contentColor: Color = Color.White,
    shadowColor: Color = Color(0xFF001A6E),
    border: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "btnScale"
    )
    val yOffset by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "btnY"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 1f else 4f,
        label = "btnElev"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = yOffset
            }
    ) {
        Button(
            onClick = {},
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(elevation.dp, RoundedCornerShape(16.dp), spotColor = shadowColor)
                .pointerInput(enabled) {
                    if (enabled) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                                onClick()
                            }
                        )
                    }
                },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor.copy(alpha = 0.4f)
            ),
            border = if (border) ButtonDefaults.outlinedButtonBorder(enabled) else null,
            contentPadding = PaddingValues(horizontal = 24.dp),
            content = content
        )
    }
}
