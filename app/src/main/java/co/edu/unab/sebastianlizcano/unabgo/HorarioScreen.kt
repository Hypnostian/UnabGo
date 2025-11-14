package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.data.local.ScheduleBlockEntity
import co.edu.unab.sebastianlizcano.unabgo.data.local.SubjectEntity
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.AcademicViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.zIndex

@Composable
fun HorarioScreen(
    navController: NavController? = null,
    viewModel: AcademicViewModel
) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    val user = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(user) {
        if (user == null) {
            navController?.navigate(Routes.LOGIN) {
                popUpTo(Routes.HORARIO) { inclusive = true }
            }
        } else {
            viewModel.setUser(user.uid)
        }
    }
    if (user == null) return

    val uiState by viewModel.uiState.collectAsState()
    val blocks by viewModel.getAllScheduleBlocks(user.uid).collectAsState(initial = emptyList())

    // 🔥 Para eliminar materia
    var subjectToDelete by remember { mutableStateOf<SubjectEntity?>(null) }

    Scaffold(
        containerColor = Color(0xFF2F024C),
        bottomBar = { BottomNavBar(navController = navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes = R.string.header_schedule
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            if (uiState.subjects.isNotEmpty()) {
                SubjectsLegend(
                    subjects = uiState.subjects,
                    openSans = openSans,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(dimens.gapM.dp))
            }

            // 🟦 CUADRÍCULA CON BLOQUES Y BOTÓN DE ELIMINAR
            ScheduleGrid(
                startHour = 6,
                endHour = 22,
                openSans = openSans,
                blocks = blocks,
                subjects = uiState.subjects,
                onDeleteClick = { subjectToDelete = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            Button(
                onClick = { navController?.navigate(Routes.SUBJECT_EDITOR) },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(dimens.buttonHeight.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5A237B),
                    contentColor = Color.White
                )
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
    }

    // CONFIRMAR ELIMINACIÓN
    if (subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Eliminar materia") },
            text = { Text("¿Seguro que deseas eliminar esta materia y su horario asociado?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val subject = subjectToDelete
                        if (subject != null) {
                            viewModel.deleteSubject(subject.id)
                        }
                        subjectToDelete = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },

            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SubjectsLegend(
    subjects: List<SubjectEntity>,
    openSans: FontFamily,
    viewModel: AcademicViewModel
) {
    var subjectToDelete by remember { mutableStateOf<SubjectEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Tus materias",
            color = Color.White,
            fontFamily = openSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        subjects.forEach { subject ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            ) {

                // Color box
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color(subject.color.toInt()), RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.name,
                        color = Color.White,
                        fontFamily = openSans,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${subject.credits} créditos",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = openSans,
                        fontSize = 12.sp
                    )
                }

                // Botón eliminar materia
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable { subjectToDelete = subject },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }

    //DIALOGO PARA ELIMINAR
    if (subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Eliminar materia") },
            text = { Text("¿Seguro que deseas eliminar esta materia y su horario?") },
            confirmButton = {
                TextButton(onClick = {
                    val subject = subjectToDelete
                    if (subject != null) {
                        viewModel.deleteSubject(subject.id)
                    }
                    subjectToDelete = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
private fun ScheduleGrid(
    startHour: Int,
    endHour: Int,
    openSans: FontFamily,
    blocks: List<ScheduleBlockEntity>,
    subjects: List<SubjectEntity>,
    onDeleteClick: (SubjectEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val hourList = (startHour..endHour).toList()
    val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")

    BoxWithConstraints(modifier = modifier) {

        val cellWidth = (maxWidth - 48.dp) / 7
        val cellHeight = 60.dp
        val headerHeight = 32.dp + 4.dp

        // 🟦 Grilla base
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hora",
                        color = Color.White,
                        fontFamily = openSans,
                        fontSize = 12.sp
                    )
                }

                dayLabels.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            color = Color.White,
                            fontFamily = openSans,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            hourList.forEach { hour ->
                Row(modifier = Modifier.fillMaxWidth()) {

                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(cellHeight),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "%02d:00".format(hour),
                            color = Color.White,
                            fontFamily = openSans,
                            fontSize = 10.sp
                        )
                    }

                    repeat(7) {
                        Box(
                            modifier = Modifier
                                .width(cellWidth)
                                .height(cellHeight)
                                .border(1.dp, Color.White.copy(alpha = 0.15f))
                        )
                    }
                }
            }
        }

        //Bloques de horario con botón
        blocks.forEach { block ->

            val subject = subjects.firstOrNull { it.id == block.subjectId } ?: return@forEach
            if (block.endMinutes <= block.startMinutes) return@forEach

            val start = block.startMinutes / 60f
            val end = block.endMinutes / 60f

            val topOffset = headerHeight + cellHeight * (start - startHour)
            val blockHeight = cellHeight * (end - start)
            val leftOffset = 48.dp + (block.dayOfWeek - 1) * cellWidth

            val bgColor = Color(subject.color.toInt())
            val textColor = if (bgColor.luminance() < 0.5f) Color.White else Color.Black

            Box(
                modifier = Modifier
                    .offset(x = leftOffset, y = topOffset)
                    .width(cellWidth)
                    .height(blockHeight)
                    .zIndex(10f)
                    .background(bgColor, RoundedCornerShape(10.dp))
                    .padding(5.dp)
            ) {

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = subject.name,
                            color = textColor,
                            fontFamily = openSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        // Botón eliminar
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(textColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .border(1.dp, textColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .clickable { onDeleteClick(subject) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", color = textColor, fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = "${block.startMinutes / 60}:00 - ${block.endMinutes / 60}:00",
                        color = textColor,
                        fontFamily = openSans,
                        fontSize = 11.sp
                    )

                    if (block.location.isNotEmpty()) {
                        Text(
                            text = block.location,
                            color = textColor.copy(alpha = 0.9f),
                            fontFamily = openSans,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
