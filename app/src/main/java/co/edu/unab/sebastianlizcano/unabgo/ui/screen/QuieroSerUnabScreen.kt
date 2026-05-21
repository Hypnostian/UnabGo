package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.data.local.Modality
import co.edu.unab.sebastianlizcano.unabgo.data.local.UnabPrograms
import co.edu.unab.sebastianlizcano.unabgo.navigation.Routes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.LocalAppDimens

/**
 * Pantalla "Quiero ser UNAB" - 100% nativa, sin WebView.
 * Muestra las 5 modalidades de programas UNAB como cards.
 * Toca una -> navega a ProgramListScreen con el detalle nativo.
 */
@Composable
fun QuieroSerUnabScreen(navController: NavController) {

    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val dimens   = LocalAppDimens.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            HeaderBar(
                navController = navController,
                subtitleRes   = R.string.header_exploring
            )

            Text(
                text       = "¿Qué quieres estudiar?",
                color      = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize   = 22.sp,
                modifier   = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 4.dp)
            )
            Text(
                text       = "Elige una modalidad para ver los programas disponibles",
                color      = Color.White.copy(alpha = 0.75f),
                fontFamily = openSans,
                fontSize   = 13.sp,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    top    = 4.dp,
                    bottom = (dimens.buttonHeight * 1.9f).dp
                )
            ) {
                items(UnabPrograms.ALL, key = { it.id }) { modality ->
                    ModalityCard(
                        modality = modality,
                        openSans = openSans,
                        onClick  = {
                            navController.navigate("programList/${modality.id}")
                        }
                    )
                }
            }
        }

        BottomNavBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ModalityCard(
    modality: Modality,
    openSans: FontFamily,
    onClick: () -> Unit
) {
    val accent = Color(modality.color)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A105D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono emoji grande con fondo de color
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = modality.emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = modality.name,
                    color      = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = modality.description,
                    color      = Color.White.copy(alpha = 0.7f),
                    fontFamily = openSans,
                    fontSize   = 12.sp
                )
                if (modality.programs.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "${modality.programs.size} programas",
                        color      = accent,
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}
