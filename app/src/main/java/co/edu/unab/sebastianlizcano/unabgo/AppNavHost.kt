package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.TeachersViewModel
import co.edu.unab.sebastianlizcano.unabgo.data.local.UnabGoDatabase
import co.edu.unab.sebastianlizcano.unabgo.data.repository.AcademicRepository
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.AcademicViewModel
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Routes.SPLASH) {

    // Contexto
    val context = LocalContext.current

    // Base de datos Room
    val db = remember { UnabGoDatabase.getInstance(context) }

    // Repositorio académico
    val academicRepository = remember {
        AcademicRepository(
            subjectDao = db.subjectDao(),
            scheduleDao = db.scheduleDao(),
            gradesDao = db.gradesDao()
        )
    }

    // ViewModel académico global para todas las pantallas
    val academicViewModel: AcademicViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AcademicViewModel(academicRepository) as T
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
            val teachersViewModel: TeachersViewModel = viewModel()
            DocentesScreen(navController = navController, viewModel = teachersViewModel)
        }
        composable(
            route = "${Routes.COMMENTS}/{teacherId}/{teacherName}"
        ) { backStackEntry ->

            val teacherId = backStackEntry.arguments?.getString("teacherId") ?: ""
            val teacherName = backStackEntry.arguments?.getString("teacherName") ?: ""

            CommentsScreen(
                navController = navController,
                teacherId = teacherId,
                teacherName = teacherName
            )
        }

    }
}
