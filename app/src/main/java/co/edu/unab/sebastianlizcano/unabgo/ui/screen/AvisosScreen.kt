package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R

import android.net.Uri
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
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.LocalAppDimens

@Composable
fun AvisosScreen(navController: NavController? = null) {

    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val dimens   = LocalAppDimens.current

    // Nuevas categorías oficiales (UNAB 2026) — URLs actualizadas
    val categoryAll       = stringResource(R.string.category_all)
    val categoryInst      = stringResource(R.string.category_institucional)
    val categoryInv       = stringResource(R.string.category_investigacion)
    val categoryCultura   = stringResource(R.string.category_cultura)
    val categoryImpacto   = stringResource(R.string.category_impacto)

    val categorias = listOf(
        categoryAll, categoryInst, categoryInv, categoryCultura, categoryImpacto
    )

    val urlCategorias = mapOf(
        categoryAll     to "https://unab.edu.co/noticias/",
        categoryInst    to "https://unab.edu.co/category/actualidad-institucional/",
        categoryInv     to "https://unab.edu.co/category/investigacion/",
        categoryCultura to "https://unab.edu.co/category/arte-cultura/",
        categoryImpacto to "https://unab.edu.co/category/historias-con-impacto/"
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
                // espacio suficiente para que el BottomNavBar no tape contenido
                .padding(bottom = (dimens.buttonHeight * 1.8f).dp)
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
                                val url = urlCategorias[categoria] ?: return@clickable
                                val encoded = Uri.encode(url)
                                navController?.navigate("newsWeb?url=$encoded")
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
                    .weight(1f)
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(navController)
        }
    }
}
