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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.AcademicViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private data class DaySchedule(
    val startHour: Int,
    val endHour: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectEditorScreen(
    navController: NavController,
    viewModel: AcademicViewModel,
    subjectId: Long? = null
) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val scope = rememberCoroutineScope()

    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.SUBJECT_EDITOR) { inclusive = true }
            }
        } else {
            viewModel.setUser(user.uid)
        }
    }

    if (user == null) return

    // 🔹 Bloques existentes del usuario (todas las materias)
    val existingBlocks by viewModel
        .getAllScheduleBlocks(user.uid)
        .collectAsState(initial = emptyList())

    // Estado de la materia
    var subjectName by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("3") }
    var selectedColor by remember { mutableStateOf(Color(0xFF6C3FB8)) }

    // Días seleccionados
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }

    // Día actualmente enfocado para editar horas
    var focusedDay by remember { mutableStateOf<Int?>(null) }

    // Horarios por día
    val scheduleByDay = remember { mutableStateMapOf<Int, DaySchedule>() }

    // Estados dropdown
    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd by remember { mutableStateOf(false) }

    val defaultStart = 8
    val defaultEnd = 10

    val currentStartHour: Int? = focusedDay?.let { day ->
        scheduleByDay[day]?.startHour ?: defaultStart
    }
    val currentEndHour: Int? = focusedDay?.let { day ->
        scheduleByDay[day]?.endHour ?: defaultEnd
    }

    val startHours = (6..21).toList()
    val endHours: List<Int> = if (currentStartHour != null) {
        ((currentStartHour + 1)..22).toList().ifEmpty { listOf(currentStartHour + 1) }
    } else {
        (7..22).toList()
    }

    // 🔹 Mensaje de conflicto de horario
    var conflictMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color(0xFF2F024C),
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes = R.string.header_schedule
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            Text(
                text = "Crear materia",
                color = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = (dimens.titleL * 0.9f).sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            LabeledField("Nombre de la materia", subjectName) { subjectName = it }

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            LabeledField("Créditos", credits) { credits = it }

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            Text(
                text = "Color de la materia",
                color = Color.White,
                fontFamily = openSans,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            ColorPickerRow(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            Text(
                text = "Horario",
                color = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            MultiDayPicker(
                selectedDays = selectedDays,
                focusedDay = focusedDay,
                onDayClick = { day ->
                    val newSet = selectedDays.toMutableSet()

                    if (newSet.contains(day)) {
                        newSet.remove(day)
                        scheduleByDay.remove(day)
                        focusedDay = newSet.firstOrNull()
                    } else {
                        newSet.add(day)
                        if (!scheduleByDay.containsKey(day)) {
                            scheduleByDay[day] = DaySchedule(defaultStart, defaultEnd)
                        }
                        focusedDay = day
                    }
                    selectedDays = newSet.toSet()
                }
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            HourDropdown(
                label = "Hora inicio",
                currentHour = currentStartHour,
                hours = startHours,
                enabled = focusedDay != null,
                expanded = expandedStart,
                onExpandedChange = { expandedStart = it },
                onSelect = { newHour ->
                    val day = focusedDay ?: return@HourDropdown
                    val current = scheduleByDay[day] ?: DaySchedule(defaultStart, defaultEnd)
                    val correctedEnd =
                        if (current.endHour <= newHour) newHour + 1 else current.endHour
                    scheduleByDay[day] = DaySchedule(
                        startHour = newHour,
                        endHour = correctedEnd.coerceAtMost(22)
                    )
                }
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            HourDropdown(
                label = "Hora fin",
                currentHour = currentEndHour,
                hours = endHours,
                enabled = focusedDay != null,
                expanded = expandedEnd,
                onExpandedChange = { expandedEnd = it },
                onSelect = { newHour ->
                    val day = focusedDay ?: return@HourDropdown
                    val current = scheduleByDay[day] ?: DaySchedule(defaultStart, defaultEnd)
                    val finalEnd = maxOf(newHour, current.startHour + 1)
                    scheduleByDay[day] = DaySchedule(
                        startHour = current.startHour,
                        endHour = finalEnd.coerceAtMost(22)
                    )
                }
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            var location by remember { mutableStateOf("") }

            LabeledField("Ubicación (opcional)", location) { location = it }

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            SaveButton("Guardar materia") {

                // ==========================================
                // 🔍 VALIDACIÓN DE CHOQUES DE HORARIO
                // ==========================================
                // Construimos los bloques NUEVOS que se quieren guardar
                val candidateBlocks = selectedDays.map { day ->
                    val s = scheduleByDay[day] ?: DaySchedule(defaultStart, defaultEnd)
                    val startMinutes = s.startHour * 60
                    val endMinutes = s.endHour * 60
                    Triple(day, startMinutes, endMinutes)
                }

                // Si no hay días seleccionados, no hay conflicto de horario
                if (candidateBlocks.isNotEmpty()) {
                    val hasConflict = candidateBlocks.any { (day, startMin, endMin) ->
                        existingBlocks.any { block ->
                            block.dayOfWeek == day &&
                                    // rango solapado: [startMin, endMin) vs [block.start, block.end)
                                    startMin < block.endMinutes &&
                                    endMin > block.startMinutes
                        }
                    }

                    if (hasConflict) {
                        conflictMessage =
                            "Ya tienes otra materia registrada en el mismo día y franja horaria. " +
                                    "Modifica el horario o desmarca el día en conflicto."
                        return@SaveButton
                    }
                }

                // ==========================================
                // ✅ SI NO HAY CONFLICTO, GUARDAMOS
                // ==========================================
                scope.launch {

                    val creditInt = credits.toIntOrNull() ?: 0

                    val argb =
                        ((selectedColor.alpha * 255).toInt() shl 24) or
                                ((selectedColor.red * 255).toInt() shl 16) or
                                ((selectedColor.green * 255).toInt() shl 8) or
                                (selectedColor.blue * 255).toInt()

                    val subjectIdResult = viewModel.saveSubject(
                        id = subjectId,
                        userId = user.uid,
                        name = subjectName,
                        color = argb.toLong(),
                        credits = creditInt
                    )

                    selectedDays.forEach { day ->
                        val s = scheduleByDay[day] ?: DaySchedule(defaultStart, defaultEnd)

                        viewModel.addScheduleBlock(
                            subjectId = subjectIdResult,
                            day = day,
                            startMinutes = s.startHour * 60,
                            endMinutes = s.endHour * 60,
                            location = location
                        )
                    }

                    navController.navigate(Routes.HORARIO) {
                        popUpTo(Routes.SUBJECT_EDITOR) { inclusive = true }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.gapL.dp))
        }

        // 🔹 Dialogo de conflicto de horario
        if (conflictMessage != null) {
            AlertDialog(
                onDismissRequest = { conflictMessage = null },
                title = {
                    Text(
                        text = "Conflicto de horario",
                        fontFamily = openSans
                    )
                },
                text = {
                    Text(
                        text = conflictMessage ?: "",
                        fontFamily = openSans,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { conflictMessage = null }) {
                        Text("Aceptar", fontFamily = openSans)
                    }
                }
            )
        }
    }
}

// ======================================================
// COMPONENTES REUTILIZABLES
// ======================================================

@Composable
private fun LabeledField(label: String, value: String, onValueChange: (String) -> Unit) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = label, color = Color.White, fontFamily = openSans, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun MultiDayPicker(
    selectedDays: Set<Int>,
    focusedDay: Int?,
    onDayClick: (Int) -> Unit
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val days = listOf("L", "M", "X", "J", "V", "S", "D")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, day ->
            val dayNumber = index + 1
            val selected = dayNumber in selectedDays
            val focused = dayNumber == focusedDay

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (selected) Color.White else Color(0xFF5A237B),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = if (focused) 2.dp else 1.dp,
                        color = if (focused) Color(0xFFFFC107) else Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onDayClick(dayNumber) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = if (selected) Color.Black else Color.White,
                    fontFamily = openSans,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HourDropdown(
    label: String,
    currentHour: Int?,
    hours: List<Int>,
    enabled: Boolean,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (Int) -> Unit
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        Text(text = label, color = Color.White, fontFamily = openSans, fontSize = 15.sp)

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) onExpandedChange(!expanded) }
        ) {
            TextField(
                value = currentHour?.let { "$it:00" } ?: "--:--",
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White.copy(alpha = 0.4f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                hours.forEach { h ->
                    DropdownMenuItem(
                        text = { Text("$h:00", fontFamily = openSans) },
                        onClick = {
                            onSelect(h)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPickerRow(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    // Más colores que funcionan bien con texto blanco
    val colors = listOf(
        Color(0xFF6C3FB8), // morado base
        Color(0xFFB83F93), // fucsia
        Color(0xFF3F92B8), // azul verdoso
        Color(0xFF3FB86B), // verde
        Color(0xFFB88F3F), // dorado
        Color(0xFFB83F3F), // rojo
        Color(0xFF2E86AB), // azul profundo
        Color(0xFF264653)  // teal oscuro
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color, shape = RoundedCornerShape(8.dp))
                    .border(
                        width = if (color == selectedColor) 3.dp else 1.dp,
                        color = if (color == selectedColor) Color.White else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun SaveButton(label: String, onClick: () -> Unit) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5A237B),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = label,
            fontFamily = openSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}
