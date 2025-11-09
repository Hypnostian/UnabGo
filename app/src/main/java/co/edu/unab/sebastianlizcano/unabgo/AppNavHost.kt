package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHost
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Routes.Splash) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.Splash) {
            SplashRoute(
                onFinished = {
                    navController.navigate("main") {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(navController = navController)
        }
        composable("quieroSerUnab") {
            QuieroSerUnabScreen(navController = navController)
        }
        composable("perfil") {
            PerfilScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable(Routes.CREDITOS) { CreditosScreen(navController) }

        composable("actualizaciones") {
            ActualizacionesScreen(navController = navController)
        }

        composable("politicaDatos") {
            PoliticaDatosScreen(navController = navController)
        }


    }
}
