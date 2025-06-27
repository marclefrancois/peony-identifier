package com.pivoinescapano.identifier.di

import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.data.loader.JsonDataLoader
import com.pivoinescapano.identifier.data.repository.FieldRepository
import com.pivoinescapano.identifier.data.repository.PeonyRepository
import com.pivoinescapano.identifier.data.repository.impl.FieldRepositoryImpl
import com.pivoinescapano.identifier.data.repository.impl.PeonyRepositoryImpl
import com.pivoinescapano.identifier.domain.usecase.FindPeonyUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldSelectionUseCase
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyIdentifierViewModel
import org.koin.dsl.module

val appModule = module {
    // Data Loading Infrastructure
    single { JsonDataLoader() }
    single { DataCacheManager(get()) }
    
    // Repositories with optimized loading
    single<FieldRepository> { FieldRepositoryImpl(get()) }
    single<PeonyRepository> { PeonyRepositoryImpl(get()) }
    
    // Use Cases
    single { GetFieldSelectionUseCase(get()) }
    single { FindPeonyUseCase(get()) }
    
    // ViewModels
    factory { PeonyIdentifierViewModel(get(), get(), get()) }
}