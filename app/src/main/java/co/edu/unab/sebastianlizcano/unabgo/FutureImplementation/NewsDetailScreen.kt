package co.edu.unab.sebastianlizcano.unabgo
/*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import coil.compose.rememberAsyncImagePainter

@Composable
fun NewsDetailScreen(navController: NavController?, newsId: String) {

    val openSans = FontFamily(Font(R.font.open_sans_regular))

    // Datos fake por ahora
    val noticia = remember(newsId) {
        fakeNewsList.find { it.id == newsId }
    }

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
                subtitleRes = R.string.announcements,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (noticia != null) {

                Image(
                    painter = rememberAsyncImagePainter(noticia.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFDFDFD)
                    )
                ) {

                    Column(modifier = Modifier.padding(20.dp)) {

                        Text(
                            text = noticia.title,
                            fontFamily = openSans,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2F024C)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${noticia.date} • ${noticia.category}",
                            fontFamily = openSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5A237B)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = noticia.summary + "\n\n" +
                                    "Contenido extendido de la noticia. " +
                                    "Aquí irá el texto completo que vendrá del backend. " +
                                    "Por ahora es un texto simulado para visualizar el diseño. " +
                                    "Esta sección soportará contenido largo y se desplazará verticalmente.",
                            fontFamily = openSans,
                            fontSize = 15.sp,
                            color = Color(0xFF222222),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavBar(navController)
        }
    }
}
*/