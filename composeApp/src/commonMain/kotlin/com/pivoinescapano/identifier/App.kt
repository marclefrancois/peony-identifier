package com.pivoinescapano.identifier

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.pivoinescapano.identifier.di.appModule
import com.pivoinescapano.identifier.presentation.screen.PeonyIdentifierScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            PeonyIdentifierScreen()
        }
    }
}