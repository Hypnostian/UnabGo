package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private data class NewsCategory(
    val name: String,
    val url: String
)

@Composable
fun AvisosScreen(navController: NavController? = null) {

    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val dimens   = LocalAppDimens.current

    // Categorias oficiales UNAB 2026 con sus URLs nuevas
    val categorias = listOf(
        NewsCategory(stringResource(R.string.category_all),           "https://unab.edu.co/noticias/"),
        NewsCategory(stringResource(R.string.category_institucional), "https://unab.edu.co/category/actualidad-institucional/"),
        NewsCategory(stringResource(R.string.category_investigacion), "https://unab.edu.co/category/investigacion/"),
        NewsCategory(stringResource(R.string.category_cultura),       "https://unab.edu.co/category/arte-cultura/"),
        NewsCategory(stringResource(R.string.category_impacto),       "https://unab.edu.co/category/historias-con-impacto/")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = (dimens.buttonHeight * 1.8f).dp)
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes   = R.string.announcements
            )

            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            // Pequeña ilustracion + texto introductorio (vertical, NO horizontal)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.banupensativo1),
                    contentDescription = "Banu Pensativo",
                    modifier           = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text       = stringResource(R.string.choose_category_message),
                    textAlign  = TextAlign.Center,
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp,
                    color      = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            // Categorias en columna vertical (cards anchas) - NO scroll horizontal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categorias.forEach { cat ->
                    CategoryCard(
                        name     = cat.name,
                        openSans = openSans,
                        onClick  = {
                            val encoded = Uri.encode(cat.url)
                            navController?.navigate("newsWeb?url=$encoded")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimens.gapM.dp))
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

@Composable
private fun CategoryCard(
    name: String,
    openSans: FontFamily,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x805A237B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 24.dp)
                    .background(Color(0xFF8E5BFF), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text       = name,
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                modifier   = Modifier.weight(1f)
            )
            Text(
                text       = "›",
                color      = Color.White.copy(alpha = 0.6f),
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
