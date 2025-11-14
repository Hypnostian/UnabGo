package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import co.edu.unab.sebastianlizcano.unabgo.data.local.CategoryWithItems
import co.edu.unab.sebastianlizcano.unabgo.data.local.GradeCategoryEntity
import co.edu.unab.sebastianlizcano.unabgo.data.local.GradeItemEntity
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.AcademicViewModel
import java.util.Locale

@Composable
fun SubjectDetailScreen(
    navController: NavController,
    viewModel: AcademicViewModel,
    subjectId: Long
) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    // Cargar detalle de la materia
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
                // Puedes cambiar a un string específico de calculadora más adelante
                subtitleRes = R.string.header_schedule
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            if (subject == null) {
                Text(
                    text = "Cargando información de la materia...",
                    color = Color.White,
                    fontFamily = openSans,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                SubjectSummaryCard(
                    name = subject.name,
                    credits = subject.credits,
                    color = Color(subject.color.toInt()),
                    average = detailState.average,
                    openSans = openSans
                )

                Spacer(modifier = Modifier.height(dimens.gapL.dp))

                CategoriesSection(
                    categories = detailState.categories,
                    subjectId = subject.id,
                    viewModel = viewModel,
                    openSans = openSans
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
private fun SubjectSummaryCard(
    name: String,
    credits: Int,
    color: Color,
    average: Float?,
    openSans: FontFamily
) {
    val avgText = average?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A105D)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(color, shape = RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        color = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "$credits créditos",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = openSans,
                        fontSize = 13.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = avgText,
                        color = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Promedio",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = openSans,
                        fontSize = 12.sp
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
    var showCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<GradeCategoryEntity?>(null) }

    var showItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<GradeItemEntity?>(null) }
    var parentCategoryIdForItem by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categorías de la nota",
                color = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                editingCategory = null
                showCategoryDialog = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar categoría",
                    tint = Color.White
                )
            }
        }

        if (categories.isEmpty()) {
            Text(
                text = "Aún no hay categorías registradas.",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = openSans,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            categories.forEach { catWithItems ->
                CategoryCard(
                    categoryWithItems = catWithItems,
                    openSans = openSans,
                    onEditCategory = {
                        editingCategory = it
                        showCategoryDialog = true
                    },
                    onDeleteCategory = { viewModel.deleteCategory(it) },
                    onAddItem = { categoryId ->
                        parentCategoryIdForItem = categoryId
                        editingItem = null
                        showItemDialog = true
                    },
                    onEditItem = { item ->
                        parentCategoryIdForItem = item.categoryId
                        editingItem = item
                        showItemDialog = true
                    },
                    onDeleteItem = { item ->
                        viewModel.deleteItem(item)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showCategoryDialog) {
        CategoryDialog(
            subjectId = subjectId,
            category = editingCategory,
            onDismiss = { showCategoryDialog = false },
            onConfirm = { name, weight ->
                if (editingCategory == null) {
                    viewModel.addCategory(subjectId = subjectId, name = name, weight = weight)
                } else {
                    viewModel.updateCategory(
                        editingCategory!!.copy(
                            name = name,
                            weightInFinal = weight
                        )
                    )
                }
                showCategoryDialog = false
            }
        )
    }

    if (showItemDialog && parentCategoryIdForItem != null) {
        ItemDialog(
            categoryId = parentCategoryIdForItem!!,
            item = editingItem,
            onDismiss = { showItemDialog = false },
            onConfirm = { name, grade, weight ->
                if (editingItem == null) {
                    viewModel.addItem(
                        categoryId = parentCategoryIdForItem!!,
                        name = name,
                        grade = grade,
                        weight = weight
                    )
                } else {
                    viewModel.updateItem(
                        editingItem!!.copy(
                            name = name,
                            grade = grade,
                            weightInCategory = weight
                        )
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
    val items = categoryWithItems.items

    val categoryAverage: Float? = if (items.isNotEmpty()) {
        val totalWeight = items.sumOf { it.weightInCategory.toDouble() }
        if (totalWeight > 0.0) {
            val sum = items.sumOf { (it.grade * it.weightInCategory).toDouble() }
            (sum / totalWeight).toFloat()
        } else null
    } else null

    val avgText = categoryAverage?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "--"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A105D)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = category.name,
                        color = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Peso en la nota final: ${category.weightInFinal}%",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = openSans,
                        fontSize = 12.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
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

                IconButton(onClick = { onEditCategory(category) }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar categoría",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { onDeleteCategory(category) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar categoría",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Items",
                    color = Color.White,
                    fontFamily = openSans,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onAddItem(category.id) }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar item",
                        tint = Color.White
                    )
                }
            }

            if (items.isEmpty()) {
                Text(
                    text = "No hay notas registradas en esta categoría.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = openSans,
                    fontSize = 12.sp
                )
            } else {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.name,
                                color = Color.White,
                                fontFamily = openSans,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Peso en categoría: ${item.weightInCategory}%",
                                color = Color.White.copy(alpha = 0.8f),
                                fontFamily = openSans,
                                fontSize = 11.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    item.grade
                                ),
                                color = Color.White,
                                fontFamily = openSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(onClick = { onEditItem(item) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Editar nota",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { onDeleteItem(item) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar nota",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryDialog(
    subjectId: Long,
    category: GradeCategoryEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, Float) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var weightText by remember { mutableStateOf(category?.weightInFinal?.toString() ?: "") }

    val openSans = FontFamily(Font(R.font.open_sans_regular))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (category == null) "Nueva categoría" else "Editar categoría",
                fontFamily = openSans
            )
        },
        text = {
            Column {
                Text(
                    text = "Nombre",
                    fontFamily = openSans,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Peso en la nota final (%)",
                    fontFamily = openSans,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.TextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weight = weightText.toFloatOrNull() ?: 0f
                    if (name.isNotBlank()) {
                        onConfirm(name, weight)
                    }
                }
            ) {
                Text("Guardar", fontFamily = openSans)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = openSans)
            }
        }
    )
}

@Composable
private fun ItemDialog(
    categoryId: Long,
    item: GradeItemEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, Float, Float) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var gradeText by remember { mutableStateOf(item?.grade?.toString() ?: "") }
    var weightText by remember { mutableStateOf(item?.weightInCategory?.toString() ?: "") }

    val openSans = FontFamily(Font(R.font.open_sans_regular))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (item == null) "Nueva nota" else "Editar nota",
                fontFamily = openSans
            )
        },
        text = {
            Column {
                Text(
                    text = "Nombre",
                    fontFamily = openSans,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.TextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nota",
                    fontFamily = openSans,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.TextField(
                    value = gradeText,
                    onValueChange = { gradeText = it },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Peso en categoría (%)",
                    fontFamily = openSans,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.TextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val grade = gradeText.toFloatOrNull() ?: 0f
                    val weight = weightText.toFloatOrNull() ?: 0f
                    if (name.isNotBlank()) {
                        onConfirm(name, grade, weight)
                    }
                }
            ) {
                Text("Guardar", fontFamily = openSans)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = openSans)
            }
        }
    )
}
