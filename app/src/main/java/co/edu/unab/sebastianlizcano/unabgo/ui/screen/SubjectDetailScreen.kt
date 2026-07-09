package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.LocalAppDimens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.data.local.CategoryWithItems
import co.edu.unab.sebastianlizcano.unabgo.data.local.GradeCategoryEntity
import co.edu.unab.sebastianlizcano.unabgo.data.local.GradeItemEntity
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.AcademicViewModel
import java.util.Locale

private fun formatNumber(value: Float?): String =
    value?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--"

private fun colorForAverage(avg: Float?): Color = when {
    avg == null -> Color(0xFFAAAAAA)
    avg >= 3.5f -> Color(0xFF4ADE80)
    avg >= 3.0f -> Color(0xFFFACC15)
    else        -> Color(0xFFF87171)
}

@Composable
fun SubjectDetailScreen(
    navController: NavController,
    viewModel: AcademicViewModel,
    subjectId: Long
) {
    val dimens   = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    LaunchedEffect(subjectId) {
        viewModel.loadSubjectDetail(subjectId)
    }

    val detailState by viewModel.detailState.collectAsState()
    val subject = detailState.subject

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
                subtitleRes   = R.string.header_schedule
            )

            Spacer(Modifier.height(dimens.gapM.dp))

            if (subject == null) {
                Text(
                    text       = "Cargando información de la materia…",
                    color      = Color.White,
                    fontFamily = openSans,
                    fontSize   = 14.sp,
                    modifier   = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    textAlign  = TextAlign.Center
                )
            } else {
                SubjectSummaryCard(
                    name     = subject.name,
                    credits  = subject.credits,
                    color    = Color(subject.color.toInt()),
                    average  = detailState.average,
                    openSans = openSans
                )

                Spacer(Modifier.height(dimens.gapM.dp))

                // Instrucciones de uso
                InfoBanner(openSans)

                Spacer(Modifier.height(dimens.gapM.dp))

                CategoriesSection(
                    categories = detailState.categories,
                    subjectId  = subject.id,
                    viewModel  = viewModel,
                    openSans   = openSans
                )
            }

            Spacer(Modifier.height(dimens.gapM.dp))
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        ) {
            BottomNavBar(navController = navController)
        }
    }
}

@Composable
private fun InfoBanner(openSans: FontFamily) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text       = "Cómo funciona",
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "• Crea categorías (Parciales, Tareas, Final…) con su peso del 100%.\n" +
                       "• En cada categoría agrega notas (0 a 5) con su peso interno.\n" +
                       "• La nota final se calcula automáticamente.",
                color      = Color.White.copy(alpha = 0.85f),
                fontFamily = openSans,
                fontSize   = 12.sp
            )
        }
    }
}

@Composable
private fun SubjectSummaryCard(
    name: String,
    credits: Int,
    color: Color,
    average: Float?,
    openSans: FontFamily
) {
    val avgText  = formatNumber(average)
    val avgColor = colorForAverage(average)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A105D)),
        shape  = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chip de color vertical
            Box(
                modifier = Modifier
                    .size(width = 8.dp, height = 56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = name,
                    color      = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )
                Text(
                    text       = "$credits créditos",
                    color      = Color.White.copy(alpha = 0.75f),
                    fontFamily = openSans,
                    fontSize   = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2F024C))
                    .border(width = 3.dp, color = avgColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = avgText,
                        color      = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                    Text(
                        text       = "/ 5",
                        color      = Color.White.copy(alpha = 0.6f),
                        fontFamily = openSans,
                        fontSize   = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<CategoryWithItems>,
    subjectId: Long,
    viewModel: AcademicViewModel,
    openSans: FontFamily
) {
    var showCategoryDialog       by remember { mutableStateOf(false) }
    var editingCategory          by remember { mutableStateOf<GradeCategoryEntity?>(null) }
    var showItemDialog           by remember { mutableStateOf(false) }
    var editingItem              by remember { mutableStateOf<GradeItemEntity?>(null) }
    var parentCategoryIdForItem  by remember { mutableStateOf<Long?>(null) }

    // Suma total de pesos de las categorías (debe ser 100%)
    val totalWeight = categories.sumOf { it.category.weightInFinal.toDouble() }.toFloat()
    val weightOk = kotlin.math.abs(totalWeight - 100f) < 0.01f

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {

        // Header con suma de pesos
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Categorías",
                    color      = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp
                )
                if (categories.isNotEmpty()) {
                    val pct = "%.0f%%".format(totalWeight)
                    Text(
                        text       = "Total de pesos: $pct ${if (weightOk) "✓" else "(debe sumar 100%)"}",
                        color      = if (weightOk) Color(0xFF4ADE80) else Color(0xFFFACC15),
                        fontFamily = openSans,
                        fontSize   = 12.sp
                    )
                }
            }

            FilledTonalIconButton(
                onClick = {
                    editingCategory    = null
                    showCategoryDialog = true
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color(0xFF8E5BFF),
                    contentColor   = Color.White
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar categoría")
            }
        }

        Spacer(Modifier.height(10.dp))

        if (categories.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text       = "Aún no hay categorías. Toca + para crear la primera.",
                    color      = Color.White.copy(alpha = 0.85f),
                    fontFamily = openSans,
                    fontSize   = 13.sp,
                    modifier   = Modifier.padding(16.dp),
                    textAlign  = TextAlign.Center
                )
            }
        } else {
            categories.forEach { catWithItems ->
                CategoryCard(
                    categoryWithItems = catWithItems,
                    openSans          = openSans,
                    onEditCategory    = {
                        editingCategory    = it
                        showCategoryDialog = true
                    },
                    onDeleteCategory  = { viewModel.deleteCategory(it) },
                    onAddItem         = { categoryId ->
                        parentCategoryIdForItem = categoryId
                        editingItem             = null
                        showItemDialog          = true
                    },
                    onEditItem        = { item ->
                        parentCategoryIdForItem = item.categoryId
                        editingItem             = item
                        showItemDialog          = true
                    },
                    onDeleteItem      = { viewModel.deleteItem(it) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    if (showCategoryDialog) {
        CategoryDialog(
            category   = editingCategory,
            onDismiss  = { showCategoryDialog = false },
            onConfirm  = { name, weight ->
                if (editingCategory == null) {
                    viewModel.addCategory(subjectId = subjectId, name = name, weight = weight)
                } else {
                    viewModel.updateCategory(
                        editingCategory!!.copy(name = name, weightInFinal = weight)
                    )
                }
                showCategoryDialog = false
            }
        )
    }

    if (showItemDialog && parentCategoryIdForItem != null) {
        ItemDialog(
            item      = editingItem,
            onDismiss = { showItemDialog = false },
            onConfirm = { name, grade, weight ->
                if (editingItem == null) {
                    viewModel.addItem(
                        categoryId = parentCategoryIdForItem!!,
                        name       = name,
                        grade      = grade,
                        weight     = weight
                    )
                } else {
                    viewModel.updateItem(
                        editingItem!!.copy(name = name, grade = grade, weightInCategory = weight)
                    )
                }
                showItemDialog = false
            }
        )
    }
}

@Composable
private fun CategoryCard(
    categoryWithItems: CategoryWithItems,
    openSans: FontFamily,
    onEditCategory: (GradeCategoryEntity) -> Unit,
    onDeleteCategory: (GradeCategoryEntity) -> Unit,
    onAddItem: (Long) -> Unit,
    onEditItem: (GradeItemEntity) -> Unit,
    onDeleteItem: (GradeItemEntity) -> Unit
) {
    val category = categoryWithItems.category
    val items    = categoryWithItems.items

    val categoryAverage: Float? = if (items.isNotEmpty()) {
        val totalW = items.sumOf { it.weightInCategory.toDouble() }
        if (totalW > 0.0) {
            val sum = items.sumOf { (it.grade * it.weightInCategory).toDouble() }
            (sum / totalW).toFloat()
        } else null
    } else null

    // Validación de pesos de ítems (debe sumar 100%)
    val itemsTotalWeight = items.sumOf { it.weightInCategory.toDouble() }.toFloat()
    val itemsWeightOk    = items.isEmpty() || kotlin.math.abs(itemsTotalWeight - 100f) < 0.01f

    val avgText  = formatNumber(categoryAverage)
    val avgColor = colorForAverage(categoryAverage)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF3A105D)),
        shape    = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header categoría
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = category.name,
                        color      = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp
                    )
                    Text(
                        text       = "Vale ${category.weightInFinal.toInt()}% de la nota final",
                        color      = Color.White.copy(alpha = 0.7f),
                        fontFamily = openSans,
                        fontSize   = 12.sp
                    )
                }

                Text(
                    text       = avgText,
                    color      = avgColor,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )
                Spacer(Modifier.width(8.dp))

                IconButton(onClick = { onEditCategory(category) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar categoría", tint = Color.White)
                }
                IconButton(onClick = { onDeleteCategory(category) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar categoría", tint = Color.White.copy(alpha = 0.7f))
                }
            }

            // Separador suave
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(vertical = 6.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            // Header items con validación de pesos
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "Notas",
                        color      = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 13.sp
                    )
                    if (items.isNotEmpty()) {
                        Text(
                            text       = "Pesos: ${itemsTotalWeight.toInt()}% ${if (itemsWeightOk) "✓" else "(deben sumar 100%)"}",
                            color      = if (itemsWeightOk) Color(0xFF4ADE80) else Color(0xFFFACC15),
                            fontFamily = openSans,
                            fontSize   = 11.sp
                        )
                    }
                }

                IconButton(onClick = { onAddItem(category.id) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar nota", tint = Color.White)
                }
            }

            if (items.isEmpty()) {
                Text(
                    text       = "Toca + para agregar la primera nota",
                    color      = Color.White.copy(alpha = 0.65f),
                    fontFamily = openSans,
                    fontSize   = 12.sp,
                    modifier   = Modifier.padding(top = 4.dp)
                )
            } else {
                items.forEach { item ->
                    GradeItemRow(
                        item       = item,
                        openSans   = openSans,
                        onEdit     = { onEditItem(item) },
                        onDelete   = { onDeleteItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GradeItemRow(
    item: GradeItemEntity,
    openSans: FontFamily,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val gradeColor = colorForAverage(item.grade)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = item.name,
                color      = Color.White,
                fontFamily = openSans,
                fontSize   = 13.sp
            )
            Text(
                text       = "Vale ${item.weightInCategory.toInt()}% de la categoría",
                color      = Color.White.copy(alpha = 0.65f),
                fontFamily = openSans,
                fontSize   = 11.sp
            )
        }

        Text(
            text       = formatNumber(item.grade),
            color      = gradeColor,
            fontFamily = openSans,
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp,
            modifier   = Modifier.padding(end = 6.dp)
        )

        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Eliminar nota",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ======================================================
// DIÁLOGOS (con tema oscuro UNAB GO)
// ======================================================

@Composable
private fun CategoryDialog(
    category: GradeCategoryEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, Float) -> Unit
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    var name        by remember { mutableStateOf(category?.name ?: "") }
    var weightText  by remember { mutableStateOf(category?.weightInFinal?.toInt()?.toString() ?: "") }
    var error       by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color(0xFF3A105D),
        titleContentColor = Color.White,
        textContentColor  = Color.White,
        title = {
            Text(
                text       = if (category == null) "Nueva categoría" else "Editar categoría",
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text("Por ejemplo: Parciales, Tareas, Examen final…",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = openSans, fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))

                ThemedTextField(
                    label   = "Nombre de la categoría",
                    value   = name,
                    onValue = { name = it; error = null }
                )
                Spacer(Modifier.height(10.dp))
                ThemedTextField(
                    label   = "Peso en la nota final (%)",
                    value   = weightText,
                    onValue = { weightText = it.filter { c -> c.isDigit() || c == '.' }; error = null },
                    keyboardType = KeyboardType.Number
                )

                if (error != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(error!!, color = Color(0xFFF87171), fontFamily = openSans, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val w = weightText.toFloatOrNull()
                when {
                    name.isBlank()           -> error = "Escribe un nombre."
                    w == null || w <= 0f     -> error = "El peso debe ser un número mayor a 0."
                    w > 100f                 -> error = "El peso no puede ser mayor a 100%."
                    else                     -> onConfirm(name.trim(), w)
                }
            }) {
                Text("Guardar", color = Color(0xFF8E5BFF), fontFamily = openSans)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White.copy(alpha = 0.75f), fontFamily = openSans)
            }
        }
    )
}

@Composable
private fun ItemDialog(
    item: GradeItemEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, Float, Float) -> Unit
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    var name       by remember { mutableStateOf(item?.name ?: "") }
    var gradeText  by remember { mutableStateOf(item?.grade?.toString() ?: "") }
    var weightText by remember { mutableStateOf(item?.weightInCategory?.toInt()?.toString() ?: "") }
    var error      by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color(0xFF3A105D),
        titleContentColor = Color.White,
        textContentColor  = Color.White,
        title = {
            Text(
                text       = if (item == null) "Nueva nota" else "Editar nota",
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text("Ejemplo: Parcial 1, Quiz 3, Proyecto final",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = openSans, fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))

                ThemedTextField(
                    label   = "Nombre",
                    value   = name,
                    onValue = { name = it; error = null }
                )
                Spacer(Modifier.height(10.dp))
                ThemedTextField(
                    label   = "Nota obtenida (0.0 a 5.0)",
                    value   = gradeText,
                    onValue = { gradeText = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                            .replace(',', '.'); error = null },
                    keyboardType = KeyboardType.Decimal
                )
                Spacer(Modifier.height(10.dp))
                ThemedTextField(
                    label   = "Peso dentro de la categoría (%)",
                    value   = weightText,
                    onValue = { weightText = it.filter { c -> c.isDigit() || c == '.' }; error = null },
                    keyboardType = KeyboardType.Number
                )

                if (error != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(error!!, color = Color(0xFFF87171), fontFamily = openSans, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val g = gradeText.toFloatOrNull()
                val w = weightText.toFloatOrNull()
                when {
                    name.isBlank()                -> error = "Escribe un nombre."
                    g == null                      -> error = "La nota debe ser un número."
                    g < 0f || g > 5f               -> error = "La nota debe estar entre 0.0 y 5.0."
                    w == null || w <= 0f           -> error = "El peso debe ser mayor a 0."
                    w > 100f                       -> error = "El peso no puede ser mayor a 100%."
                    else -> onConfirm(name.trim(), g, w)
                }
            }) {
                Text("Guardar", color = Color(0xFF8E5BFF), fontFamily = openSans)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White.copy(alpha = 0.75f), fontFamily = openSans)
            }
        }
    )
}

@Composable
private fun ThemedTextField(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    Column {
        Text(label, color = Color.White, fontFamily = openSans, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value           = value,
            onValueChange   = onValue,
            singleLine      = true,
            modifier        = Modifier.fillMaxWidth(),
            shape           = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor       = Color.White,
                unfocusedTextColor     = Color.White,
                focusedBorderColor     = Color(0xFF8E5BFF),
                unfocusedBorderColor   = Color.White.copy(alpha = 0.4f),
                cursorColor            = Color(0xFF8E5BFF)
            )
        )
    }
}
