package com.pivoinescapano.identifier.di

import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.data.loader.JsonDataLoader
import com.pivoinescapano.identifier.data.repository.FieldNotesRepositoryImpl
import com.pivoinescapano.identifier.data.repository.FieldRepository
import com.pivoinescapano.identifier.data.repository.PeonyRepository
import com.pivoinescapano.identifier.data.repository.impl.FieldRepositoryImpl
import com.pivoinescapano.identifier.data.repository.impl.PeonyRepositoryImpl
import com.pivoinescapano.identifier.data.usecase.GetFieldEntriesUseCase
import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import com.pivoinescapano.identifier.domain.service.ExportService
import com.pivoinescapano.identifier.domain.usecase.ClearAllNotesUseCase
import com.pivoinescapano.identifier.domain.usecase.CreateFieldNoteUseCase
import com.pivoinescapano.identifier.domain.usecase.DeleteFieldNoteUseCase
import com.pivoinescapano.identifier.domain.usecase.ExportFieldNotesUseCase
import com.pivoinescapano.identifier.domain.usecase.FindPeonyUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldNotesUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldSelectionUseCase
import com.pivoinescapano.identifier.domain.usecase.SearchPeonyLocationsUseCase
import com.pivoinescapano.identifier.domain.usecase.UpdateFieldNoteUseCase
import com.pivoinescapano.identifier.platform.provideFileSharing
import com.pivoinescapano.identifier.presentation.viewmodel.FieldNotesViewModel
import com.pivoinescapano.identifier.presentation.viewmodel.FieldSelectionViewModel
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyDetailViewModel
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyIdentifierViewModel
import com.pivoinescapano.identifier.presentation.viewmodel.PeonySearchViewModel
import org.koin.dsl.module

val appModule =
    module {
        // Data Loading Infrastructure
        single { JsonDataLoader() }
        single { DataCacheManager(get()) }

        // Platform Services
        single { provideFileSharing() }
        single { ExportService(get(), get()) }

        // Repositories with optimized loading
        single<FieldRepository> { FieldRepositoryImpl(get()) }
        single<PeonyRepository> { PeonyRepositoryImpl(get()) }
        single<FieldNotesRepository> { FieldNotesRepositoryImpl(get()) }

        // Use Cases
        single { GetFieldSelectionUseCase(get()) }
        single { GetFieldEntriesUseCase(get()) }
        single { FindPeonyUseCase(get()) }
        single { SearchPeonyLocationsUseCase(get()) }

        // Field Notes Use Cases
        single { CreateFieldNoteUseCase(get()) }
        single { UpdateFieldNoteUseCase(get()) }
        single { DeleteFieldNoteUseCase(get()) }
        single { GetFieldNotesUseCase(get()) }
        single { ExportFieldNotesUseCase(get()) }
        single { ClearAllNotesUseCase(get()) }

        // ViewModels
        factory { FieldSelectionViewModel(get()) }
        factory { (champ: String, parcelle: String) ->
            PeonyIdentifierViewModel(champ, parcelle, get(), get(), get())
        }
        factory { (champ: String, parcelle: String, rang: String, trou: String) ->
            PeonyDetailViewModel(champ, parcelle, rang, trou, get(), get(), get(), get(), get())
        }
        factory { PeonySearchViewModel(get()) }
        factory { FieldNotesViewModel(get(), get()) }
    }
