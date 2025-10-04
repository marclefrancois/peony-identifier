package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinInject(),
) {
    val authState by viewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()

    val googleAuthProvider =
        GoogleAuthProvider.create(
            credentials =
                GoogleAuthCredentials(
                    serverId = "818858644321-np6aurrg466eqr0utd9dq2tltlo22uvd.apps.googleusercontent.com",
                ),
        )
    val googleAuthUiProvider = googleAuthProvider.getUiProvider()
    val scopes =
        listOf(
            "email",
            "profile",
            "https://www.googleapis.com/auth/drive.readonly",
            "https://www.googleapis.com/auth/spreadsheets.readonly",
        )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp),
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = AppColors.PrimaryGreen,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Peony Identifier",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign in with Google to access your peony data",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (authState.isLoading) {
                CircularProgressIndicator(
                    color = AppColors.PrimaryGreen,
                )
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.signInWithProvider(googleAuthUiProvider, scopes)
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AppColors.PrimaryGreen,
                            contentColor = AppColors.OnPrimary,
                        ),
                ) {
                    Text("Sign in with Google")
                }
            }
        }
    }
}
