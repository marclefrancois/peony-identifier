@file:Suppress("FunctionName")

package com.pivoinescapano.identifier

import androidx.compose.ui.window.ComposeUIViewController
import com.pivoinescapano.identifier.di.appModule
import com.pivoinescapano.identifier.di.platformModule
import org.koin.core.context.startKoin

@Suppress("unused")
fun MainViewController() =
    ComposeUIViewController {
        startKoin {
            modules(appModule, platformModule)
        }
        App()
    }
