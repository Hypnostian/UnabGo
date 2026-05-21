package co.edu.unab.sebastianlizcano.unabgo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.edu.unab.sebastianlizcano.unabgo.UnabGoApplication
import co.edu.unab.sebastianlizcano.unabgo.data.repository.TeachersRepository
import co.edu.unab.sebastianlizcano.unabgo.ui.screen.*
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.AcademicViewModel
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.CommentsViewModel
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.TeachersViewModel

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Routes.SPLASH) {

    // Contexto
    val context = LocalContext.current

    // Obtiene la instancia del Application para acceder a las dependencias globales
    val app = remember { context.applicationContext as UnabGoApplication } // Manual DI

    // Repositorio académico desde Application (Singleton compartido)
    val academicRepository = remember { app.academicRepository } // Singleton

    // Repositorio de docentes desde Application (Singleton compartido — evita 2 conexiones Firestore)
    val teachersRepository: TeachersRepository = remember { app.teachersRepository } // Singleton

    // ViewModel académico global para todas las pantallas de horario y calculadora
    val academicViewModel: AcademicViewModel = viewModel(
        factory = object : ViewModelProvider.Factory { // Factory Pattern
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AcademicViewModel(academicRepository) as T // Manual Dependency Injection
            }
        }
    )

    // ViewModel de docentes — comparte el mismo repositorio con CommentsViewModel
    val teachersViewModel: TeachersViewModel = viewModel(
        factory = object : ViewModelProvider.Factory { // Factory Pattern
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TeachersViewModel(teachersRepository) as T // Manual Dependency Injection
            }
        }
    )

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

        composable(Routes.AVISOS) {
            AvisosScreen(navController)
        }

        composable(
            route = "newsWeb?url={url}",
            arguments = listOf(navArgument("url") { defaultValue = "" })
        ) {
            val url = it.arguments?.getString("url")
            NewsWebScreen(navController = navController, url = url)
        }

        // Detalle nativo de una noticia (via WordPress REST API)
        composable(
            route     = Routes.NEWS_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.LongType })
        ) { entry ->
            val postId = entry.arguments?.getLong("postId") ?: 0L
            NewsDetailScreen(navController = navController, postId = postId)
        }

        composable(Routes.MATERIAL_ESTUDIO) {
            MaterialEstudioScreen(navController = navController)
        }

        // Pantalla Horario
        composable(Routes.HORARIO) {
            HorarioScreen(
                navController = navController,
                viewModel = academicViewModel
            )
        }

        // Pantalla Calculadora
        composable(Routes.CALCULADORA) {
            CalculadoraScreen(
                navController = navController,
                viewModel = academicViewModel
            )
        }

        // Crear materia
        composable(Routes.SUBJECT_EDITOR) {
            SubjectEditorScreen(
                navController = navController,
                viewModel = academicViewModel,
                subjectId = null
            )
        }

        // Editar materia
        composable(
            route = Routes.SUBJECT_EDITOR_WITH_ID,
            arguments = listOf(navArgument("subjectId") { defaultValue = "0" })
        ) { entry ->
            val subjectId = entry.arguments?.getString("subjectId")?.toLongOrNull() ?: 0L

            SubjectEditorScreen(
                navController = navController,
                viewModel = academicViewModel,
                subjectId = subjectId
            )
        }

        // Detalle de notas
        composable(
            route = Routes.CALCULADORA_DETALLE,
            arguments = listOf(navArgument("subjectId") { defaultValue = "0" })
        ) { entry ->
            val subjectId = entry.arguments?.getString("subjectId")?.toLongOrNull() ?: 0L

            SubjectDetailScreen(
                navController = navController,
                viewModel = academicViewModel,
                subjectId = subjectId
            )
        }

        composable(Routes.DOCENTES) {
            DocentesScreen(navController = navController, viewModel = teachersViewModel) // Singleton compartido
        }

        composable(
            route = "${Routes.COMMENTS}/{teacherId}/{teacherName}"
        ) { backStackEntry ->

            val teacherId   = backStackEntry.arguments?.getString("teacherId") ?: ""
            val teacherName = backStackEntry.arguments?.getString("teacherName") ?: ""

            // CommentsViewModel comparte el mismo teachersRepository (evita 2 conexiones Firestore)
            val commentsViewModel: CommentsViewModel = viewModel(
                factory = object : ViewModelProvider.Factory { // Factory Pattern
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return CommentsViewModel(teachersRepository) as T // Manual Dependency Injection
                    }
                }
            )

            CommentsScreen(
                navController = navController,
                teacherId     = teacherId,
                teacherName   = teacherName,
                viewModel     = commentsViewModel
            )
        }

        composable(Routes.BANU_IA) {
            BanuIAScreen(navController = navController)
        }

        composable(Routes.MAPA) {
            MapaInteractivoScreen(navController = navController)
        }

        composable(
            route = "${Routes.WEBVIEW_DETAIL}?url={url}&title={title}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType; nullable = false; defaultValue = "https://unab.edu.co" },
                navArgument("title") { type = NavType.StringType; nullable = false; defaultValue = "Detalle" }
            )
        ) { backStackEntry ->
            val url   = backStackEntry.arguments?.getString("url") ?: "https://unab.edu.co"
            val title = backStackEntry.arguments?.getString("title") ?: "Detalle"

            WebViewDetailScreen(
                navController = navController,
                url           = url,
                title         = title
            )
        }

    }
}
