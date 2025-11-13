package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar

@Composable
fun AvisosScreen(navController: NavController? = null) {

    val openSans = FontFamily(Font(R.font.open_sans_regular))

    // Categorías traducidas desde strings
    val categorias = listOf(
        stringResource(R.string.category_institucional),
        stringResource(R.string.category_investigacion),
        stringResource(R.string.category_salud),
        stringResource(R.string.category_ingenieria),
        stringResource(R.string.category_cultura),
        stringResource(R.string.category_derecho),
        stringResource(R.string.category_impacto)
    )

    val urlCategorias = mapOf(
        stringResource(R.string.category_institucional) to "https://unab.edu.co/category/institucional/",
        stringResource(R.string.category_investigacion) to "https://unab.edu.co/category/investigacion/",
        stringResource(R.string.category_salud) to "https://unab.edu.co/category/salud/",
        stringResource(R.string.category_ingenieria) to "https://unab.edu.co/category/ingenieria/",
        stringResource(R.string.category_cultura) to "https://unab.edu.co/category/cultura-y-humanidades/",
        stringResource(R.string.category_derecho) to "https://unab.edu.co/category/derecho-y-negocios/",
        stringResource(R.string.category_impacto) to "https://unab.edu.co/category/impacto-social/"
    )

    var categoriaSeleccionada by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes = R.string.announcements,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scroll horizontal de categorías
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                categorias.forEach { categoria ->

                    val seleccionado = categoria == categoriaSeleccionada

                    Card(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable {
                                categoriaSeleccionada = categoria
                                val url = urlCategorias[categoria]!!
                                navController?.navigate("newsWeb?url=$url")
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado)
                                Color(0xFF7B2AFF) else Color(0x33FFFFFF)
                        )
                    ) {
                        Text(
                            text = categoria,
                            fontFamily = openSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Pantalla central (si no han seleccionado categoría)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.banupensativo1),
                    contentDescription = "Banu Pensativo",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.choose_category_message),
                    textAlign = TextAlign.Center,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavBar(navController)
        }
    }
}
