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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// 12 colores armónicos que destacan sobre el fondo morado #2F024C
private data class SubjectColor(val name: String, val value: Color)

private val SUBJECT_COLORS = listOf(
    SubjectColor("Violeta",    Color(0xFF8E5BFF)),
    SubjectColor("Magenta",    Color(0xFFE94BC4)),
    SubjectColor("Rosa",       Color(0xFFFF6B9D)),
    SubjectColor("Coral",      Color(0xFFFF7B5C)),
    SubjectColor("Naranja",    Color(0xFFFF9F40)),
    SubjectColor("Amarillo",   Color(0xFFFFCC4D)),
    SubjectColor("Verde lima", Color(0xFFA8DA52)),
    SubjectColor("Esmeralda",  Color(0xFF2EC4B6)),
    SubjectColor("Turquesa",   Color(0xFF4CC9F0)),
    SubjectColor("Azul",       Color(0xFF4361EE)),
    SubjectColor("Índigo",     Color(0xFF7209B7)),
    SubjectColor("Lavanda",    Color(0xFFC9B6FF))
)

// Estructura para horario por día (hora inicio + hora fin)
private data class DayHours(val start: Int, val end: Int)

private const val DEFAULT_START = 8
private const val DEFAULT_END   = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectEditorScreen(
    navController: NavController,
    viewModel: AcademicViewModel,
    subjectId: Long? = null
) {
    val dimens   = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val scope    = rememberCoroutineScope()

    val user = FirebaseAuth.getInstance().currentUser
    val isEditing = subjectId != null && subjectId != 0L

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

    val existingBlocks by viewModel
        .getAllScheduleBlocks(user.uid)
        .collectAsState(initial = emptyList())

    // ===== Estado del formulario =====
    var subjectName   by remember { mutableStateOf("") }
    var credits       by remember { mutableStateOf("3") }
    var location      by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(SUBJECT_COLORS[0].value) }
    var selectedDays  by remember { mutableStateOf(setOf<Int>()) }

    // Modo horario unico vs distinto por dia
    var sameScheduleAllDays by remember { mutableStateOf(true) }

    // Modo unico: una sola hora global
    var globalStart by remember { mutableStateOf(DEFAULT_START) }
    var globalEnd   by remember { mutableStateOf(DEFAULT_END) }

    // Modo por dia: hora por cada dia seleccionado
    val perDayHours = remember { mutableStateMapOf<Int, DayHours>() }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var dataLoaded   by remember { mutableStateOf(!isEditing) }

    // ===== Cargar datos existentes al EDITAR =====
    LaunchedEffect(isEditing, subjectId) {
        if (isEditing && subjectId != null) {
            viewModel.loadSubjectDetail(subjectId)
        }
    }

    val detailState by viewModel.detailState.collectAsState()

    // Cuando carga el detalle de la materia a editar, prellenamos el formulario.
    LaunchedEffect(detailState.subject, detailState.schedule) {
        val s = detailState.subject
        if (isEditing && !dataLoaded && s != null && s.id == subjectId) {
            subjectName   = s.name
            credits       = s.credits.toString()
            selectedColor = Color(s.color.toInt())

            val blocks = detailState.schedule
            selectedDays = blocks.map { it.dayOfWeek }.toSet()

            // Llenar horarios por día
            perDayHours.clear()
            blocks.forEach { b ->
                perDayHours[b.dayOfWeek] = DayHours(b.startMinutes / 60, b.endMinutes / 60)
            }

            // Detectar si todos los días tienen el mismo horario
            val unique = blocks.map { it.startMinutes to it.endMinutes }.distinct()
            sameScheduleAllDays = unique.size <= 1
            if (sameScheduleAllDays && blocks.isNotEmpty()) {
                globalStart = blocks.first().startMinutes / 60
                globalEnd   = blocks.first().endMinutes / 60
            }

            // Ubicación: tomamos la del primer bloque (asumiendo es la misma para todos)
            location = blocks.firstOrNull()?.location.orEmpty()

            dataLoaded = true
        }
    }

    Scaffold(
        containerColor = Color(0xFF2F024C),
        bottomBar      = { BottomNavBar(navController = navController) }
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

            Text(
                text       = if (isEditing) "Editar materia" else "Nueva materia",
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize   = (dimens.titleL * 0.9f).sp,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(dimens.gapM.dp))

            LabeledField("Nombre de la materia", subjectName) { subjectName = it }

            Spacer(Modifier.height(dimens.gapM.dp))

            LabeledField("Créditos", credits) { credits = it.filter { c -> c.isDigit() } }

            Spacer(Modifier.height(dimens.gapL.dp))

            SectionTitle("Color de la materia")
            Spacer(Modifier.height(10.dp))
            ColorPickerGrid(
                selectedColor   = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            Spacer(Modifier.height(dimens.gapL.dp))

            // ===== Días de clase =====
            SectionTitle("Días de clase")
            Spacer(Modifier.height(6.dp))
            Text(
                text       = "Toca los días en que se dicta la materia",
                color      = Color.White.copy(alpha = 0.7f),
                fontFamily = openSans,
                fontSize   = 12.sp,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(10.dp))
            DayPickerCalendarStyle(
                selectedDays  = selectedDays,
                accentColor   = selectedColor,
                onDayToggle   = { day ->
                    selectedDays = if (day in selectedDays) {
                        perDayHours.remove(day)
                        selectedDays - day
                    } else {
                        // Inicializa la hora del nuevo día (copia las globales)
                        if (!sameScheduleAllDays && !perDayHours.containsKey(day)) {
                            perDayHours[day] = DayHours(globalStart, globalEnd)
                        }
                        selectedDays + day
                    }
                }
            )

            Spacer(Modifier.height(dimens.gapL.dp))

            // ===== Horario =====
            SectionTitle("Horario")
            Spacer(Modifier.height(6.dp))

            // Toggle: misma hora todos los días vs hora distinta por día
            ScheduleModeToggle(
                same = sameScheduleAllDays,
                onChange = { newValue ->
                    sameScheduleAllDays = newValue
                    if (!newValue) {
                        // Cambia a modo "por día": inicializa con la hora global
                        selectedDays.forEach { d ->
                            if (!perDayHours.containsKey(d)) {
                                perDayHours[d] = DayHours(globalStart, globalEnd)
                            }
                        }
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            if (sameScheduleAllDays) {
                // ----- MODO 1: una sola franja para todos los días -----
                Text(
                    text       = "Esta franja aplica a todos los días seleccionados",
                    color      = Color.White.copy(alpha = 0.7f),
                    fontFamily = openSans,
                    fontSize   = 12.sp,
                    modifier   = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(10.dp))

                HourRangePicker(
                    startHour = globalStart,
                    endHour   = globalEnd,
                    onChange  = { s, e ->
                        globalStart = s
                        globalEnd   = e
                    }
                )
            } else {
                // ----- MODO 2: una franja por cada día -----
                if (selectedDays.isEmpty()) {
                    Text(
                        text       = "Primero selecciona los días arriba",
                        color      = Color.White.copy(alpha = 0.65f),
                        fontFamily = openSans,
                        fontSize   = 12.sp,
                        modifier   = Modifier.padding(horizontal = 20.dp)
                    )
                } else {
                    selectedDays.sorted().forEach { day ->
                        val dayLabel = dayLongName(day)
                        val current  = perDayHours[day] ?: DayHours(DEFAULT_START, DEFAULT_END)

                        DayScheduleRow(
                            dayName     = dayLabel,
                            startHour   = current.start,
                            endHour     = current.end,
                            accentColor = selectedColor,
                            onChange    = { s, e ->
                                perDayHours[day] = DayHours(s, e)
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(dimens.gapM.dp))

            LabeledField("Ubicación (opcional)", location) { location = it }

            Spacer(Modifier.height(dimens.gapL.dp))

            // Preview de la materia
            if (selectedDays.isNotEmpty() && subjectName.isNotBlank()) {
                SubjectPreview(
                    name      = subjectName,
                    color     = selectedColor,
                    days      = selectedDays,
                    same      = sameScheduleAllDays,
                    globalStart = globalStart,
                    globalEnd   = globalEnd,
                    perDayHours = perDayHours,
                    location  = location
                )
                Spacer(Modifier.height(dimens.gapL.dp))
            }

            // Botón guardar
            SaveButton(
                label       = if (isEditing) "Guardar cambios" else "Crear materia",
                accentColor = selectedColor
            ) {
                // Construir los bloques candidatos según el modo
                val candidateBlocks = selectedDays.map { day ->
                    val s = if (sameScheduleAllDays) globalStart else
                        perDayHours[day]?.start ?: globalStart
                    val e = if (sameScheduleAllDays) globalEnd else
                        perDayHours[day]?.end ?: globalEnd
                    Triple(day, s * 60, e * 60)
                }

                // Validaciones
                when {
                    subjectName.isBlank() ->
                        errorMessage = "Escribe el nombre de la materia."
                    credits.toIntOrNull() == null || (credits.toIntOrNull() ?: 0) <= 0 ->
                        errorMessage = "Los créditos deben ser un número mayor a 0."
                    selectedDays.isEmpty() ->
                        errorMessage = "Selecciona al menos un día de clase."
                    candidateBlocks.any { it.third <= it.second } ->
                        errorMessage = "La hora de fin debe ser mayor que la de inicio en todos los días."
                    else -> {
                        // Conflictos con OTRAS materias (se excluyen los bloques de ESTA materia
                        // si estamos editando — para que no marque conflicto consigo misma)
                        val hasConflict = candidateBlocks.any { (day, startMin, endMin) ->
                            existingBlocks.any { block ->
                                block.subjectId != (subjectId ?: -1L) &&
                                block.dayOfWeek == day &&
                                startMin < block.endMinutes &&
                                endMin   > block.startMinutes
                            }
                        }
                        if (hasConflict) {
                            errorMessage = "Ya tienes otra materia en ese día y franja horaria."
                            return@SaveButton
                        }

                        scope.launch {
                            val argb =
                                ((selectedColor.alpha * 255).toInt() shl 24) or
                                ((selectedColor.red   * 255).toInt() shl 16) or
                                ((selectedColor.green * 255).toInt() shl 8)  or
                                 (selectedColor.blue  * 255).toInt()

                            val savedId = viewModel.saveSubject(
                                id      = subjectId,
                                userId  = user.uid,
                                name    = subjectName.trim(),
                                color   = argb.toLong(),
                                credits = credits.toIntOrNull() ?: 3
                            )

                            // Al editar, borramos primero los bloques antiguos
                            if (isEditing) {
                                viewModel.deleteAllBlocksForSubject(savedId)
                            }

                            // Agregamos los nuevos bloques
                            candidateBlocks.forEach { (day, sMin, eMin) ->
                                viewModel.addScheduleBlock(
                                    subjectId    = savedId,
                                    day          = day,
                                    startMinutes = sMin,
                                    endMinutes   = eMin,
                                    location     = location
                                )
                            }

                            navController.navigate(Routes.HORARIO) {
                                popUpTo(Routes.SUBJECT_EDITOR) { inclusive = true }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(dimens.gapL.dp * 2))
        }

        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("No se pudo guardar", fontFamily = openSans) },
                text  = { Text(errorMessage ?: "", fontFamily = openSans, fontSize = 14.sp) },
                confirmButton = {
                    TextButton(onClick = { errorMessage = null }) {
                        Text("Entendido", fontFamily = openSans)
                    }
                }
            )
        }
    }
}

// ======================================================
// COMPONENTES
// ======================================================

private fun dayLongName(day: Int): String = when (day) {
    1 -> "Lunes";   2 -> "Martes";   3 -> "Miércoles"
    4 -> "Jueves";  5 -> "Viernes";  6 -> "Sábado";   7 -> "Domingo"
    else -> "Día $day"
}

private fun dayShortName(day: Int): String = when (day) {
    1 -> "Lun"; 2 -> "Mar"; 3 -> "Mié"
    4 -> "Jue"; 5 -> "Vie"; 6 -> "Sáb"; 7 -> "Dom"
    else -> "?"
}

@Composable
private fun SectionTitle(text: String) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    Text(
        text       = text,
        color      = Color.White,
        fontFamily = openSans,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        modifier   = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun LabeledField(label: String, value: String, onValueChange: (String) -> Unit) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = label, color = Color.White, fontFamily = openSans, fontSize = 15.sp)
        Spacer(Modifier.height(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor    = Color.White,
                unfocusedContainerColor  = Color.White,
                focusedIndicatorColor    = Color.Transparent,
                unfocusedIndicatorColor  = Color.Transparent
            )
        )
    }
}

@Composable
private fun ScheduleModeToggle(
    same: Boolean,
    onChange: (Boolean) -> Unit
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = "Misma hora todos los días",
                color      = Color.White,
                fontFamily = openSans,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (same) "La franja aplica a todos los días seleccionados"
                       else      "Cada día tiene su propio horario",
                color      = Color.White.copy(alpha = 0.7f),
                fontFamily = openSans,
                fontSize   = 12.sp
            )
        }
        Switch(
            checked = same,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = Color(0xFF8E5BFF),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.25f)
            )
        )
    }
}

@Composable
private fun DayPickerCalendarStyle(
    selectedDays: Set<Int>,
    accentColor: Color,
    onDayToggle: (Int) -> Unit
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val days = listOf("L", "M", "M", "J", "V", "S", "D")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, label ->
            val dayNumber = index + 1
            val selected  = dayNumber in selectedDays
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (selected) accentColor else Color.White.copy(alpha = 0.10f))
                    .border(
                        width = 1.5.dp,
                        color = if (selected) accentColor else Color.White.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
                    .clickable { onDayToggle(dayNumber) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = label,
                    color      = Color.White,
                    fontFamily = openSans,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ColorPickerGrid(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SUBJECT_COLORS.chunked(4).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowColors.forEach { sc ->
                    val isSelected = sc.value == selectedColor
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(sc.value)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(sc.value) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Color seleccionado",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Selector de hora inicio/fin lado a lado (modo "misma hora"). */
@Composable
private fun HourRangePicker(
    startHour: Int,
    endHour: Int,
    onChange: (Int, Int) -> Unit
) {
    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd   by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HourDropdown(
            label    = "Inicia",
            value    = startHour,
            hours    = (6..21).toList(),
            expanded = expandedStart,
            onExpand = { expandedStart = it },
            onSelect = { newStart ->
                val newEnd = if (endHour <= newStart) (newStart + 1).coerceAtMost(22) else endHour
                onChange(newStart, newEnd)
            },
            modifier = Modifier.weight(1f)
        )
        HourDropdown(
            label    = "Termina",
            value    = endHour,
            hours    = ((startHour + 1)..22).toList(),
            expanded = expandedEnd,
            onExpand = { expandedEnd = it },
            onSelect = { newEnd -> onChange(startHour, newEnd) },
            modifier = Modifier.weight(1f)
        )
    }
}

/** Fila con día + selector inicio/fin (modo "horas distintas"). */
@Composable
private fun DayScheduleRow(
    dayName: String,
    startHour: Int,
    endHour: Int,
    accentColor: Color,
    onChange: (Int, Int) -> Unit
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd   by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text       = dayName,
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HourDropdown(
                label    = "Inicia",
                value    = startHour,
                hours    = (6..21).toList(),
                expanded = expandedStart,
                onExpand = { expandedStart = it },
                onSelect = { newStart ->
                    val newEnd = if (endHour <= newStart) (newStart + 1).coerceAtMost(22) else endHour
                    onChange(newStart, newEnd)
                },
                modifier = Modifier.weight(1f)
            )
            HourDropdown(
                label    = "Termina",
                value    = endHour,
                hours    = ((startHour + 1)..22).toList(),
                expanded = expandedEnd,
                onExpand = { expandedEnd = it },
                onSelect = { newEnd -> onChange(startHour, newEnd) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HourDropdown(
    label: String,
    value: Int,
    hours: List<Int>,
    expanded: Boolean,
    onExpand: (Boolean) -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Column(modifier = modifier) {
        Text(text = label, color = Color.White, fontFamily = openSans, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpand(!expanded) }
        ) {
            TextField(
                value         = "%02d:00".format(value),
                onValueChange = {},
                readOnly      = true,
                modifier      = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape         = RoundedCornerShape(10.dp),
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { onExpand(false) }
            ) {
                hours.forEach { h ->
                    DropdownMenuItem(
                        text    = { Text("%02d:00".format(h), fontFamily = openSans) },
                        onClick = {
                            onSelect(h)
                            onExpand(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectPreview(
    name: String,
    color: Color,
    days: Set<Int>,
    same: Boolean,
    globalStart: Int,
    globalEnd: Int,
    perDayHours: Map<Int, DayHours>,
    location: String
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    val scheduleText = if (same) {
        val daysText = days.sorted().joinToString(", ") { dayShortName(it) }
        "$daysText · %02d:00 – %02d:00".format(globalStart, globalEnd)
    } else {
        days.sorted().joinToString(" · ") { d ->
            val hrs = perDayHours[d] ?: DayHours(globalStart, globalEnd)
            "${dayShortName(d)} %02d:00–%02d:00".format(hrs.start, hrs.end)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape  = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = name,
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = scheduleText,
                color      = Color.White.copy(alpha = 0.95f),
                fontFamily = openSans,
                fontSize   = 13.sp
            )
            if (location.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = location,
                    color      = Color.White.copy(alpha = 0.85f),
                    fontFamily = openSans,
                    fontSize   = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SaveButton(label: String, accentColor: Color, onClick: () -> Unit) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    Button(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor   = Color.White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text       = label,
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp
        )
    }
}
