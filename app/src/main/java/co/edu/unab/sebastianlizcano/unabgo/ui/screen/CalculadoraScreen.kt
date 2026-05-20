package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.LocalAppDimens
import co.edu.unab.sebastianlizcano.unabgo.navigation.Routes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.data.local.SubjectEntity
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.AcademicViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

/**
 * Devuelve un color según el valor del promedio (escala 0-5):
 *   >= 3.5  -> verde (excelente)
 *   3.0-3.49 -> amarillo (aprobado justo)
 *   < 3.0   -> rojo (en riesgo)
 *   null    -> gris claro
 */
private fun colorForAverage(avg: Float?): Color = when {
    avg == null    -> Color(0xFFAAAAAA)
    avg >= 3.5f    -> Color(0xFF4ADE80)  // verde
    avg >= 3.0f    -> Color(0xFFFACC15)  // amarillo
    else           -> Color(0xFFF87171)  // rojo
}

private fun formatGrade(value: Float?): String =
    value?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--"

@Composable
fun CalculadoraScreen(
    navController: NavController,
    viewModel: AcademicViewModel
) {
    val dimens   = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.CALCULADORA) { inclusive = true }
            }
        } else {
            viewModel.setUser(user.uid)
        }
    }

    if (user == null) return

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF2F024C),
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.SUBJECT_EDITOR) },
                containerColor = Color(0xFF8E5BFF),
                contentColor   = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar materia")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes   = R.string.header_schedule
            )

            Spacer(Modifier.height(dimens.gapM.dp))

            // ===== Promedio global con visual =====
            GlobalAverageCard(
                average     = uiState.globalAverage,
                openSans    = openSans,
                subjectsCount = uiState.subjects.size
            )

            Spacer(Modifier.height(dimens.gapL.dp))

            if (uiState.subjects.isEmpty()) {
                EmptySubjectsMessage(openSans)
            } else {
                SubjectsAverageList(
                    subjects        = uiState.subjects,
                    subjectAverages = uiState.subjectAverages,
                    openSans        = openSans,
                    onDetailClick   = { id -> navController.navigate("calculatorDetail/$id") },
                    onEditClick     = { id -> navController.navigate("subjectEditor/$id") }
                )
            }

            Spacer(Modifier.height(dimens.gapL.dp * 2))
        }
    }
}

// ======================================================
// COMPONENTES
// ======================================================

@Composable
private fun GlobalAverageCard(
    average: Float?,
    openSans: FontFamily,
    subjectsCount: Int
) {
    val avgColor = colorForAverage(average)
    val avgText  = formatGrade(average)
    val message  = when {
        average == null      -> "Aún no hay notas registradas"
        average >= 4.0f      -> "¡Excelente!"
        average >= 3.5f      -> "¡Vas muy bien!"
        average >= 3.0f      -> "Vas aprobando"
        else                 -> "¡Ánimo, puedes mejorar!"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A105D)),
        shape  = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "Promedio acumulado",
                color      = Color.White,
                fontFamily = openSans,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(14.dp))

            // Círculo de promedio con borde de color según valor
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2F024C))
                    .border(width = 6.dp, color = avgColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = avgText,
                        color      = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 36.sp
                    )
                    Text(
                        text       = "de 5.00",
                        color      = Color.White.copy(alpha = 0.7f),
                        fontFamily = openSans,
                        fontSize   = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text       = message,
                color      = avgColor,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp
            )

            if (subjectsCount > 0) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = "$subjectsCount ${if (subjectsCount == 1) "materia registrada" else "materias registradas"}",
                    color      = Color.White.copy(alpha = 0.6f),
                    fontFamily = openSans,
                    fontSize   = 12.sp
                )
            }
        }
    }
}

@Composable
private fun EmptySubjectsMessage(openSans: FontFamily) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape  = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "Aún no has registrado materias",
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text       = "Toca el botón + para crear tu primera materia y empezar a calcular tu promedio.",
                color      = Color.White.copy(alpha = 0.75f),
                fontFamily = openSans,
                fontSize   = 13.sp,
                textAlign  = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SubjectsAverageList(
    subjects: List<SubjectEntity>,
    subjectAverages: Map<Long, Float?>,
    openSans: FontFamily,
    onDetailClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text       = "Promedio por materia",
            color      = Color.White,
            fontFamily = openSans,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 16.sp,
            modifier   = Modifier.padding(bottom = 10.dp)
        )

        subjects.forEach { subject ->
            val avg = subjectAverages[subject.id]
            SubjectAverageCard(
                subject       = subject,
                average       = avg,
                openSans      = openSans,
                onDetailClick = { onDetailClick(subject.id) },
                onEditClick   = { onEditClick(subject.id) }
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SubjectAverageCard(
    subject: SubjectEntity,
    average: Float?,
    openSans: FontFamily,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val avgText  = formatGrade(average)
    val avgColor = colorForAverage(average)
    val subjectColor = Color(subject.color.toInt())

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onDetailClick() },
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF3A105D)),
        shape    = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Color de la materia (chip vertical)
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 48.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(subjectColor)
            )

            Spacer(Modifier.width(12.dp))

            // Nombre y créditos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = subject.name,
                    color      = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
                Text(
                    text       = "${subject.credits} créditos",
                    color      = Color.White.copy(alpha = 0.7f),
                    fontFamily = openSans,
                    fontSize   = 12.sp
                )
            }

            // Promedio con color
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = avgText,
                    color      = avgColor,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp
                )
                Text(
                    text       = "Promedio",
                    color      = Color.White.copy(alpha = 0.6f),
                    fontFamily = openSans,
                    fontSize   = 10.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector        = Icons.Filled.Edit,
                    contentDescription = "Editar materia",
                    tint               = Color.White.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector        = Icons.Filled.ChevronRight,
                contentDescription = "Ver detalle",
                tint               = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

