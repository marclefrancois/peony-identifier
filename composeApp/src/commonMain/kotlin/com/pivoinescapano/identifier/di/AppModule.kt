package com.pivoinescapano.identifier.di

import com.pivoinescapano.identifier.data.repository.FieldRepository
import com.pivoinescapano.identifier.data.repository.PeonyRepository
import com.pivoinescapano.identifier.data.repository.impl.FieldRepositoryImpl
import com.pivoinescapano.identifier.data.repository.impl.PeonyRepositoryImpl
import com.pivoinescapano.identifier.domain.usecase.FindPeonyUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldSelectionUseCase
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyIdentifierViewModel
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single<FieldRepository> { FieldRepositoryImpl() }
    single<PeonyRepository> { PeonyRepositoryImpl() }
    
    // Use Cases
    single { GetFieldSelectionUseCase(get()) }
    single { FindPeonyUseCase(get()) }
    
    // ViewModels
    factory { PeonyIdentifierViewModel(get(), get()) }
}