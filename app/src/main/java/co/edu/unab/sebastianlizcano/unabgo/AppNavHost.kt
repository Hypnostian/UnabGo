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
        composable(Routes.SOY_UNAB) {
            SoyUnabScreen(navController = navController)
        }
        composable(Routes.CHECKING) {
            CheckingScreen(navController = navController)
        }

        /*
        composable(Routes.MATERIAL_ESTUDIO) { MaterialEstudioScreen(navController) }
        composable(Routes.CALCULADORA) { CalculadoraScreen(navController) }
        composable(Routes.HORARIO) { HorarioScreen(navController) }
        composable(Routes.AVISOS) { AvisosScreen(navController) }
        composable(Routes.DOCENTES) { DocentesScreen(navController) }
        composable(Routes.CHECKING) { CheckingScreen(navController) }
        composable(Routes.MAPA) { MapaScreen(navController) }
         */
    }
}
