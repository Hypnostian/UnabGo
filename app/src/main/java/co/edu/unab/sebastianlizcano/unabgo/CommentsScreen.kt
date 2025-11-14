package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.Comment
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.CommentsViewModel

@Composable
fun CommentsScreen(
    navController: NavController,
    teacherId: String,
    teacherName: String,
    viewModel: CommentsViewModel = viewModel()
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    LaunchedEffect(teacherId) {
        viewModel.loadComments(teacherId)
    }

    val comments = viewModel.comments
    val isLoading = viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        // HeaderBar agregado
        HeaderBar(
            navController = navController
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Comentarios de $teacherName",
                fontFamily = openSans,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Text(
                    text = "Cargando...",
                    color = Color.White,
                    fontFamily = openSans
                )
            } else {
                LazyColumn {
                    items(comments) { c: Comment ->
                        CommentCard(text = c.text)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCard(text: String) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                color = Color.Black
            )
        }
    }
}
