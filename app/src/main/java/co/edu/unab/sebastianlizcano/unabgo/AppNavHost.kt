package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Routes.SPLASH) {

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SPLASH) {
            SplashRoute(
                onFinished = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }

        composable(Routes.QUIERO_SER_UNAB) {
            QuieroSerUnabScreen(navController = navController)
        }

        composable(Routes.PERFIL) {
            PerfilScreen(navController = navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onBackClick = { navController.popBackStack() }
            )
        }


        composable(Routes.ACTUALIZACIONES) {
            ActualizacionesScreen(navController = navController)
        }

        composable(Routes.POLITICADATOS) {
            PoliticaDatosScreen(navController = navController)
        }

        composable(Routes.CREDITOS) {
            CreditosScreen(navController = navController)
        }
    }
}
