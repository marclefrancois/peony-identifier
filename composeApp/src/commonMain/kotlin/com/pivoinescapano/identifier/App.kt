package com.pivoinescapano.identifier

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import com.pivoinescapano.identifier.presentation.screen.PeonyDetailScreen
import com.pivoinescapano.identifier.presentation.screen.PeonyIdentifierScreen
import com.pivoinescapano.identifier.presentation.screen.PeonySearchScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule, platformModule)
    }) {
        MaterialTheme {
            // v1.3 Fix: Add proper background for iOS
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
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
                        // Check for restored state from savedStateHandle (handles iOS gestures)
                        val restoredChamp = backStackEntry.savedStateHandle.get<String>("restoredChamp")
                        val restoredParcelle = backStackEntry.savedStateHandle.get<String>("restoredParcelle")

                        FieldSelectionScreen(
                            initialChamp = restoredChamp ?: route.initialChamp,
                            initialParcelle = restoredParcelle ?: route.initialParcelle,
                            onContinue = { champ, parcelle ->
                                // Replace current FieldSelection with one that has preserved state
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
                        // Check for restored state from savedStateHandle (handles returning from detail)
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
                                // Store state in the previous back stack entry (FieldSelection)
                                navController.previousBackStackEntry?.savedStateHandle?.let { savedState ->
                                    savedState["restoredChamp"] = champ
                                    savedState["restoredParcelle"] = parcelle
                                }
                            },
                            onUpdateSelectionState = { rang, trou ->
                                // Store selection state in current back stack entry for return from detail
                                backStackEntry.savedStateHandle["restoredRang"] = rang
                                backStackEntry.savedStateHandle["restoredTrou"] = trou
                            },
                        )
                    }

                    composable<PeonyDetailRoute> { backStackEntry ->
                        val route = backStackEntry.toRoute<PeonyDetailRoute>()
                        // Check for restored search term from savedStateHandle
                        val restoredSearchTerm = backStackEntry.savedStateHandle.get<String>("restoredSearchTerm")

                        PeonyDetailScreen(
                            champ = route.champ,
                            parcelle = route.parcelle,
                            rang = route.rang,
                            trou = route.trou,
                            onNavigateBack = {
                                // Store search term if we came from search
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
                        // Check for restored search term from savedStateHandle
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
        }
    }
}
