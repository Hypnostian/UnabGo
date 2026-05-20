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
// Todos tienen buen contraste con texto blanco/negro.
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

    // Bloques existentes del usuario (todas las materias)
    val existingBlocks by viewModel
        .getAllScheduleBlocks(user.uid)
        .collectAsState(initial = emptyList())

    // Estado de la materia
    var subjectName   by remember { mutableStateOf("") }
    var credits       by remember { mutableStateOf("3") }
    var location      by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(SUBJECT_COLORS[0].value) }

    // Días seleccionados (1=Lun, 7=Dom)
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }

    // UNA sola franja horaria que aplica a TODOS los días seleccionados (intuitivo)
    var startHour by remember { mutableStateOf(8) }
    var endHour   by remember { mutableStateOf(10) }

    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd   by remember { mutableStateOf(false) }

    // Mensaje de validación
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                text       = if (subjectId == null || subjectId == 0L) "Nueva materia" else "Editar materia",
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize   = (dimens.titleL * 0.9f).sp,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(dimens.gapM.dp))

            // === Nombre, créditos, ubicación ===
            LabeledField("Nombre de la materia", subjectName) { subjectName = it }

            Spacer(Modifier.height(dimens.gapM.dp))

            LabeledField("Créditos", credits) { credits = it }

            Spacer(Modifier.height(dimens.gapL.dp))

            // === Color (grid 4x3 con 12 colores) ===
            SectionTitle("Color de la materia")
            Spacer(Modifier.height(10.dp))
            ColorPickerGrid(
                selectedColor   = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            Spacer(Modifier.height(dimens.gapL.dp))

            // === Días de clase (estilo Google Calendar) ===
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
                    selectedDays = if (day in selectedDays) selectedDays - day
                                   else selectedDays + day
                }
            )

            Spacer(Modifier.height(dimens.gapL.dp))

            // === Horario ===
            SectionTitle("Horario")
            Spacer(Modifier.height(6.dp))
            Text(
                text       = "Esta franja horaria aplica a todos los días seleccionados",
                color      = Color.White.copy(alpha = 0.7f),
                fontFamily = openSans,
                fontSize   = 12.sp,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HourDropdown(
                    label     = "Inicia",
                    value     = startHour,
                    hours     = (6..21).toList(),
                    expanded  = expandedStart,
                    onExpand  = { expandedStart = it },
                    onSelect  = { h ->
                        startHour = h
                        if (endHour <= h) endHour = (h + 1).coerceAtMost(22)
                    },
                    modifier  = Modifier.weight(1f)
                )
                HourDropdown(
                    label     = "Termina",
                    value     = endHour,
                    hours     = ((startHour + 1)..22).toList(),
                    expanded  = expandedEnd,
                    onExpand  = { expandedEnd = it },
                    onSelect  = { endHour = it },
                    modifier  = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(dimens.gapM.dp))

            LabeledField("Ubicación (opcional)", location) { location = it }

            Spacer(Modifier.height(dimens.gapL.dp))

            // === Resumen visual antes de guardar ===
            if (selectedDays.isNotEmpty() && subjectName.isNotBlank()) {
                SubjectPreview(
                    name      = subjectName,
                    color     = selectedColor,
                    days      = selectedDays,
                    startHour = startHour,
                    endHour   = endHour,
                    location  = location
                )
                Spacer(Modifier.height(dimens.gapL.dp))
            }

            // === Botón guardar ===
            SaveButton(
                label       = if (subjectId == null || subjectId == 0L) "Crear materia" else "Guardar cambios",
                accentColor = selectedColor
            ) {
                // Validaciones
                when {
                    subjectName.isBlank() ->
                        errorMessage = "Escribe el nombre de la materia."
                    credits.toIntOrNull() == null || (credits.toIntOrNull() ?: 0) <= 0 ->
                        errorMessage = "Los créditos deben ser un número mayor a 0."
                    selectedDays.isEmpty() ->
                        errorMessage = "Selecciona al menos un día de clase."
                    endHour <= startHour ->
                        errorMessage = "La hora de fin debe ser mayor que la de inicio."
                    else -> {
                        // Verificar choques de horario
                        val startMinutes = startHour * 60
                        val endMinutes   = endHour * 60
                        val hasConflict  = selectedDays.any { day ->
                            existingBlocks.any { block ->
                                block.dayOfWeek == day &&
                                startMinutes < block.endMinutes &&
                                endMinutes   > block.startMinutes
                            }
                        }
                        if (hasConflict) {
                            errorMessage = "Ya tienes otra materia en ese día y franja horaria."
                            return@SaveButton
                        }

                        // Todo OK - guardar
                        scope.launch {
                            val argb =
                                ((selectedColor.alpha * 255).toInt() shl 24) or
                                ((selectedColor.red   * 255).toInt() shl 16) or
                                ((selectedColor.green * 255).toInt() shl 8)  or
                                 (selectedColor.blue  * 255).toInt()

                            val savedId = viewModel.saveSubject(
                                id      = subjectId,
                                userId  = user.uid,
                                name    = subjectName,
                                color   = argb.toLong(),
                                credits = credits.toIntOrNull() ?: 3
                            )

                            selectedDays.forEach { day ->
                                viewModel.addScheduleBlock(
                                    subjectId    = savedId,
                                    day          = day,
                                    startMinutes = startMinutes,
                                    endMinutes   = endMinutes,
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

        // Diálogo de error
        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("No se pudo guardar", fontFamily = openSans) },
                text  = {
                    Text(
                        text       = errorMessage ?: "",
                        fontFamily = openSans,
                        fontSize   = 14.sp
                    )
                },
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
// COMPONENTES REUTILIZABLES
// ======================================================

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

/**
 * Selector de días estilo Google Calendar: chips circulares L M M J V S D.
 * Visual feedback con el color de la materia.
 */
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
                    .background(
                        color = if (selected) accentColor else Color.White.copy(alpha = 0.10f)
                    )
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

/**
 * Grid de 12 colores en 3 filas de 4 columnas.
 * El color seleccionado muestra un checkmark blanco.
 */
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
        Text(text = label, color = Color.White, fontFamily = openSans, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))

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
                shape = RoundedCornerShape(10.dp),
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

/**
 * Tarjeta de preview que muestra cómo se verá la materia antes de guardarla.
 */
@Composable
private fun SubjectPreview(
    name: String,
    color: Color,
    days: Set<Int>,
    startHour: Int,
    endHour: Int,
    location: String
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val dayNames = mapOf(1 to "Lun", 2 to "Mar", 3 to "Mié", 4 to "Jue", 5 to "Vie", 6 to "Sáb", 7 to "Dom")
    val daysText = days.sorted().joinToString(", ") { dayNames[it] ?: "" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape  = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text       = name,
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "$daysText · %02d:00 – %02d:00".format(startHour, endHour),
                color      = Color.White.copy(alpha = 0.95f),
                fontFamily = openSans,
                fontSize   = 14.sp
            )
            if (location.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = location,
                    color      = Color.White.copy(alpha = 0.85f),
                    fontFamily = openSans,
                    fontSize   = 13.sp
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
