package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.data.remote.Teacher
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.TeachersViewModel


@Composable
fun DocentesScreen(
    navController: NavController? = null,
    viewModel: TeachersViewModel = viewModel()
) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    val teachers = viewModel.teachers
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    var query by rememberSaveable { mutableStateOf("") }

    val filteredTeachers = remember(teachers, query) {
        if (query.isBlank()) teachers
        else teachers.filter {
            it.fullName.contains(query, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = (dimens.buttonHeight * 2f).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderBar(
                navController = navController,
                subtitleRes = R.string.header_teachers
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            SearchBar(
                value = query,
                onValueChange = { query = it },
                openSans = openSans,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.gapL.dp)
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            when {
                isLoading -> {
                    Text(
                        text = stringResource(id = R.string.loading),
                        color = Color.White,
                        fontFamily = openSans,
                        fontSize = 14.sp
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = "Error: ${errorMessage}",
                        color = Color.Red,
                        fontFamily = openSans,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                filteredTeachers.isEmpty() -> {
                    Text(
                        text = "No se encontraron docentes.",
                        color = Color.White,
                        fontFamily = openSans,
                        fontSize = 14.sp
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.gapL.dp)
                    ) {
                        filteredTeachers.forEach { teacher ->
                            TeacherCard(
                                teacher = teacher,
                                openSans = openSans,
                                viewModel = viewModel,
                                navController = navController
                            )
                            Spacer(modifier = Modifier.height(dimens.gapS.dp))
                        }
                    }
                }
            }
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
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    openSans: FontFamily,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(Color.White, shape = RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF5A237B),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = Color(0xFF2F024C),
                    fontFamily = openSans,
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = "Búsquedas con Banu",
                            color = Color(0xFF9C8AC4),
                            fontFamily = openSans,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun TeacherCard(
    teacher: Teacher,
    openSans: FontFamily,
    viewModel: TeachersViewModel,
    navController: NavController?
) {
    var commentText by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.perfil),
                        contentDescription = "Docente",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = teacher.fullName,
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color(0xFF2F024C)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    StarsRow(
                        rating = teacher.rating,
                        onRatingSelected = { newRating ->
                            viewModel.updateRating(teacher.id, newRating.toDouble()) {}
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            BasicTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F3F3), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 13.sp,
                    fontFamily = openSans
                ),
                decorationBox = { inner ->
                    if (commentText.isEmpty()) {
                        Text(
                            text = "Escribe un comentario...",
                            fontFamily = openSans,
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                    inner()
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // VER COMENTARIOS
                Button(
                    onClick = {
                        navController?.navigate(
                            "${Routes.COMMENTS}/${teacher.id}/${teacher.fullName}"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8E0FF),
                        contentColor = Color(0xFF5A237B)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "Ver comentarios (${teacher.commentsCount})",
                        fontFamily = openSans,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // ENVIAR COMENTARIO
                Button(
                    onClick = {
                        viewModel.addComment(teacher.id, commentText) { success ->
                            if (success) commentText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD6C4FF),
                        contentColor = Color(0xFF2F024C)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "Enviar",
                        fontFamily = openSans,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StarsRow(
    rating: Double,
    onRatingSelected: (Int) -> Unit,
    maxStars: Int = 5
) {
    var selected by remember { mutableStateOf(rating.toInt()) }

    Row {
        for (i in 1..maxStars) {
            val filled = i <= selected

            Icon(
                painter = painterResource(
                    id = if (filled) R.drawable.star_filled else R.drawable.star_outline
                ),
                contentDescription = "Rating",
                tint = if (filled) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(22.dp)
                    .padding(end = 2.dp)
                    .clickable {
                        selected = i
                        onRatingSelected(i)
                    }
            )
        }
    }
}
