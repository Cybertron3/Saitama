package com.ankitesh.saitama.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ankitesh.saitama.ui.HomeScreen
import com.ankitesh.saitama.ui.WeightTrackerScreen
import com.ankitesh.saitama.ui.WeightViewModel
import com.ankitesh.saitama.ui.cig.CigTrackerScreen
import com.ankitesh.saitama.ui.cig.CigViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object WeightDetail : Screen("weight_detail")
    object CigDetail : Screen("cig_detail")
    object CigDetailWithTargetDialog : Screen("cig_detail?openTargetDialog=true")
}

@Composable
fun SaitamaNavHost(
    navController: NavHostController = rememberNavController(),
    weightViewModel: WeightViewModel,
    cigViewModel: CigViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                weightViewModel = weightViewModel,
                cigViewModel = cigViewModel,
                onNavigateToWeightDetail = {
                    navController.navigate(Screen.WeightDetail.route)
                },
                onNavigateToCigDetail = { openTargetDialog ->
                    if (openTargetDialog) {
                        navController.navigate(Screen.CigDetailWithTargetDialog.route)
                    } else {
                        navController.navigate(Screen.CigDetail.route)
                    }
                }
            )
        }

        composable(Screen.WeightDetail.route) {
            WeightTrackerScreen(
                viewModel = weightViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CigDetail.route) {
            CigTrackerScreen(
                viewModel = cigViewModel,
                openTargetDialog = false,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CigDetailWithTargetDialog.route) {
            CigTrackerScreen(
                viewModel = cigViewModel,
                openTargetDialog = true,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
