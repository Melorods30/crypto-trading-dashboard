package com.cryptotrading.dashboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cryptotrading.dashboard.ui.screens.CryptoDetailScreen
import com.cryptotrading.dashboard.ui.screens.DashboardScreen

/**
 * Rotas da aplicação
 */
object Routes {
    const val DASHBOARD = "dashboard"
    const val CRYPTO_DETAIL = "crypto_detail/{cryptoId}"

    fun cryptoDetailRoute(cryptoId: String) = "crypto_detail/$cryptoId"
}

/**
 * Componente principal de navegação da aplicação
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        // Tela principal do Dashboard
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onCryptoClick = { cryptoId ->
                    navController.navigate(Routes.cryptoDetailRoute(cryptoId))
                }
            )
        }

        // Tela de detalhes da criptomoeda
        composable(
            route = Routes.CRYPTO_DETAIL,
            arguments = listOf(
                navArgument("cryptoId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: ""

            CryptoDetailScreen(
                cryptoId = cryptoId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
