package com.pivoinescapano.identifier

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pivoinescapano.identifier.data.model.LoadingState
import com.pivoinescapano.identifier.di.appModule
import com.pivoinescapano.identifier.di.platformModule
import com.pivoinescapano.identifier.presentation.navigation.FieldNotesRoute
import com.pivoinescapano.identifier.presentation.navigation.FieldSelectionRoute
import com.pivoinescapano.identifier.presentation.navigation.HomeRoute
import com.pivoinescapano.identifier.presentation.navigation.PeonyDetailRoute
import com.pivoinescapano.identifier.presentation.navigation.PeonyIdentifierRoute
import com.pivoinescapano.identifier.presentation.navigation.PeonySearchRoute
import com.pivoinescapano.identifier.presentation.screen.FieldNotesScreen
import com.pivoinescapano.identifier.presentation.screen.FieldSelectionScreen
import com.pivoinescapano.identifier.presentation.screen.HomeScreen
import com.pivoinescapano.identifier.presentation.screen.LoadingSplashScreen
import com.pivoinescapano.identifier.presentation.screen.PeonyDetailScreen
import com.pivoinescapano.identifier.presentation.screen.PeonyIdentifierScreen
import com.pivoinescapano.identifier.presentation.screen.PeonySearchScreen
import com.pivoinescapano.identifier.presentation.viewmodel.LoadingViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule, platformModule)
    }) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                val loadingViewModel: LoadingViewModel = koinInject()
                val loadingState by loadingViewModel.loadingState.collectAsState()

                when (loadingState) {
                    is LoadingState.Loading -> {
                        LoadingSplashScreen(loadingState = loadingState)
                    }
                    is LoadingState.Error -> {
                        LoadingSplashScreen(loadingState = loadingState)
                    }
                    is LoadingState.Success -> {
                        MainNavigationHost()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MainNavigationHost() {
    val navController = rememberNavController()
    LocalDensity.current

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToSearch = {
                    navController.navigate(PeonySearchRoute)
                },
                onNavigateToIdentify = {
                    navController.navigate(FieldSelectionRoute())
                },
                onNavigateToFieldNotes = {
                    navController.navigate(FieldNotesRoute)
                },
            )
        }

        composable<FieldSelectionRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<FieldSelectionRoute>()
            val restoredChamp = backStackEntry.savedStateHandle.get<String>("restoredChamp")
            val restoredParcelle = backStackEntry.savedStateHandle.get<String>("restoredParcelle")

            FieldSelectionScreen(
                initialChamp = restoredChamp ?: route.initialChamp,
                initialParcelle = restoredParcelle ?: route.initialParcelle,
                onContinue = { champ, parcelle ->
                    navController.navigate(FieldSelectionRoute(champ, parcelle)) {
                        popUpTo<FieldSelectionRoute> { inclusive = true }
                    }
                    navController.navigate(PeonyIdentifierRoute(champ, parcelle))
                },
                onNavigateBack = {
                    navController.navigateUp()
                },
            )
        }

        composable<PeonyIdentifierRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PeonyIdentifierRoute>()
            val restoredRang = backStackEntry.savedStateHandle.get<String>("restoredRang")
            val restoredTrou = backStackEntry.savedStateHandle.get<String>("restoredTrou")

            PeonyIdentifierScreen(
                selectedChamp = route.champ,
                selectedParcelle = route.parcelle,
                initialSelectedRang = restoredRang ?: route.selectedRang,
                initialSelectedTrou = restoredTrou ?: route.selectedTrou,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToDetail = { champ, parcelle, rang, trou ->
                    navController.navigate(PeonyDetailRoute(champ, parcelle, rang, trou, fromSearchTerm = null))
                },
                onUpdateBackStackState = { champ, parcelle ->
                    navController.previousBackStackEntry?.savedStateHandle?.let { savedState ->
                        savedState["restoredChamp"] = champ
                        savedState["restoredParcelle"] = parcelle
                    }
                },
                onUpdateSelectionState = { rang, trou ->
                    backStackEntry.savedStateHandle["restoredRang"] = rang
                    backStackEntry.savedStateHandle["restoredTrou"] = trou
                },
            )
        }

        composable<PeonyDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PeonyDetailRoute>()
            val restoredSearchTerm = backStackEntry.savedStateHandle.get<String>("restoredSearchTerm")

            PeonyDetailScreen(
                champ = route.champ,
                parcelle = route.parcelle,
                rang = route.rang,
                trou = route.trou,
                onNavigateBack = {
                    if (route.fromSearchTerm != null || restoredSearchTerm != null) {
                        navController.previousBackStackEntry?.savedStateHandle?.let { savedState ->
                            savedState["restoredSearchTerm"] = route.fromSearchTerm ?: restoredSearchTerm
                        }
                    }
                    navController.navigateUp()
                },
            )
        }

        composable<PeonySearchRoute> { backStackEntry ->
            val restoredSearchTerm = backStackEntry.savedStateHandle.get<String>("restoredSearchTerm")

            PeonySearchScreen(
                restoredSearchTerm = restoredSearchTerm,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToDetail = { champ, parcelle, rang, trou, searchTerm ->
                    navController.navigate(PeonyDetailRoute(champ, parcelle, rang, trou, searchTerm))
                },
            )
        }

        composable<FieldNotesRoute> {
            FieldNotesScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToDetail = { champ, parcelle, rang, trou ->
                    navController.navigate(PeonyDetailRoute(champ, parcelle, rang, trou, fromSearchTerm = null))
                },
            )
        }
    }
}
