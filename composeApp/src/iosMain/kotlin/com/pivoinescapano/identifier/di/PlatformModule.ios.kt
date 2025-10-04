package com.pivoinescapano.identifier.di

import com.pivoinescapano.identifier.data.auth.GoogleAuthService
import com.pivoinescapano.identifier.data.storage.FileSystemStorage
import org.koin.dsl.module

actual val platformModule =
    module {
        single { FileSystemStorage() }
        single { GoogleAuthService() }
    }
