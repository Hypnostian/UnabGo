package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun CalculadoraScreen(
    navController: NavController,
    viewModel: AcademicViewModel
) {
    val dimens = LocalAppDimens.current
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
                .verticalScroll(rememberScrollState())
        ) {

            HeaderBar(
                navController = navController,
                // Puedes cambiar este string más adelante por uno específico de calculadora
                subtitleRes = R.string.header_schedule
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            // Promedio global en círculo
            GlobalAverageCircle(
                average = uiState.globalAverage,
                openSans = openSans
            )

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            // Lista de materias
            if (uiState.subjects.isEmpty()) {
                EmptySubjectsMessage(openSans = openSans)
            } else {
                SubjectsAverageList(
                    subjects = uiState.subjects,
                    subjectAverages = uiState.subjectAverages,
                    openSans = openSans,
                    onDetailClick = { subjectId ->
                        navController.navigate("calculatorDetail/$subjectId")
                    },
                    onEditClick = { subjectId ->
                        navController.navigate("subjectEditor/$subjectId")
                    }
                )
            }

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            // Botón para agregar materia
            Button(
                onClick = { navController.navigate(Routes.SUBJECT_EDITOR) },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(dimens.buttonHeight.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5A237B),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Agregar materia",
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = (dimens.body * 1.05f).sp
                )
            }

            Spacer(modifier = Modifier.height(dimens.gapM.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(navController = navController)
        }
    }
}

@Composable
private fun GlobalAverageCircle(
    average: Float?,
    openSans: FontFamily
) {
    val averageText = average?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Promedio global",
            color = Color.White,
            fontFamily = openSans,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color(0xFF5A237B), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = averageText,
                color = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptySubjectsMessage(openSans: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aún no has registrado materias.",
            color = Color.White,
            fontFamily = openSans,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Agrega una materia para comenzar a calcular tu promedio.",
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = openSans,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Promedio por materia",
            color = Color.White,
            fontFamily = openSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        subjects.forEach { subject ->
            val avg = subjectAverages[subject.id]
            SubjectAverageCard(
                subject = subject,
                average = avg,
                openSans = openSans,
                onDetailClick = { onDetailClick(subject.id) },
                onEditClick = { onEditClick(subject.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
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
    val avgText = average?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--"

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A105D)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Color de la materia
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(Color(subject.color.toInt()), shape = RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Nombre y créditos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subject.name,
                    color = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${subject.credits} créditos",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = openSans,
                    fontSize = 12.sp
                )
            }

            // Promedio de la materia
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = avgText,
                    color = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Promedio",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = openSans,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Botón editar
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar materia",
                    tint = Color.White
                )
            }

            // Botón detalle
            IconButton(onClick = onDetailClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Ver detalle",
                    tint = Color.White
                )
            }
        }
    }
}
