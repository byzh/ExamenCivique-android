package com.examencivique.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examencivique.data.repository.AuthRepository
import com.examencivique.data.repository.AuthResult
import com.examencivique.ui.i18n.LocalStrings
import com.examencivique.ui.theme.FrenchBlue
import com.examencivique.ui.theme.FrenchRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    authRepo: AuthRepository,
    onBack: () -> Unit
) {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.authForgotPasswordTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, s.back)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null; successMessage = null },
                label = { Text(s.authEmail) },
                leadingIcon = { Icon(Icons.Filled.Email, null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMessage != null) {
                Text(errorMessage!!, color = FrenchRed, fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            if (successMessage != null) {
                Text(successMessage!!, color = Color(0xFF2E7D32), fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            Button(
                onClick = {
                    errorMessage = null
                    successMessage = null
                    isLoading = true
                    scope.launch {
                        when (val result = authRepo.resetPassword(email.trim())) {
                            is AuthResult.Success -> successMessage = s.authResetEmailSent
                            is AuthResult.Error -> {
                                if (result.message == "Email sent") {
                                    successMessage = s.authResetEmailSent
                                } else {
                                    errorMessage = s.authErrorMap(result.message)
                                }
                            }
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading && email.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FrenchBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(s.authSendResetEmail, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
