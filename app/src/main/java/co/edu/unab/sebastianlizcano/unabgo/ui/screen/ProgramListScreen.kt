package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.data.local.ProgramItem
import co.edu.unab.sebastianlizcano.unabgo.data.local.UnabPrograms

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

/**
 * Pantalla de detalle de una modalidad: lista los programas reales de UNAB
 * (datos locales verificados, sin WebView).
 *  - Si la modalidad tiene programas listados (caso "Pregrados"): muestra la lista
 *  - Si NO tiene programas (Posgrados, Tecnicos, etc.): muestra un boton que abre
 *    la pagina general de UNAB en el navegador externo del telefono.
 */
@Composable
fun ProgramListScreen(
    navController: NavController,
    modalityId: String
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val dimens   = LocalAppDimens.current
    val context  = LocalContext.current

    val modality = UnabPrograms.findById(modalityId)
    val accent   = modality?.color?.let { Color(it) } ?: Color(0xFF8E5BFF)

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

            if (modality == null) {
                Text(
                    text       = "Modalidad no encontrada",
                    color      = Color.White,
                    fontFamily = openSans,
                    modifier   = Modifier.fillMaxWidth().padding(32.dp),
                    textAlign  = TextAlign.Center
                )
                return@Column
            }

            // Header de la modalidad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accent.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = modality.emoji, fontSize = 28.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text       = modality.name,
                        color      = Color.White,
                        fontFamily = openSans,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    )
                    Text(
                        text       = modality.description,
                        color      = Color.White.copy(alpha = 0.7f),
                        fontFamily = openSans,
                        fontSize   = 13.sp
                    )
                }
            }

            // Contenido segun si hay programas listados
            if (modality.programs.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(
                        top    = 4.dp,
                        bottom = (dimens.buttonHeight * 1.9f).dp
                    )
                ) {
                    items(modality.programs, key = { it.slug }) { program ->
                        ProgramCard(
                            program  = program,
                            accent   = accent,
                            openSans = openSans,
                            onClick  = { openInBrowser(context, program.url) }
                        )
                    }

                    // Boton para ver TODOS los pregrados en la web oficial
                    item {
                        Spacer(Modifier.height(8.dp))
                        modality.externalUrl?.let { externalUrl ->
                            Button(
                                onClick = { openInBrowser(context, externalUrl) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accent,
                                    contentColor   = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.OpenInBrowser,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text       = "Ver todos en unab.edu.co",
                                    fontFamily = openSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 14.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Modalidad sin programas listados: solo boton al sitio oficial
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = (dimens.buttonHeight * 1.9f).dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text       = "Conoce la oferta completa de ${modality.name.lowercase()} en la página oficial UNAB",
                        color      = Color.White.copy(alpha = 0.9f),
                        fontFamily = openSans,
                        fontSize   = 15.sp,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    modality.externalUrl?.let { externalUrl ->
                        Button(
                            onClick = { openInBrowser(context, externalUrl) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent,
                                contentColor   = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text       = "Ver en unab.edu.co",
                                fontFamily = openSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 15.sp
                            )
                        }
                    }
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
private fun ProgramCard(
    program: ProgramItem,
    accent: Color,
    openSans: FontFamily,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A105D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = program.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = program.name,
                    color      = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = program.shortDescription,
                    color      = Color.White.copy(alpha = 0.75f),
                    fontFamily = openSans,
                    fontSize   = 12.sp,
                    maxLines   = 2
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Abrir",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

/** Abre una URL en el navegador externo del teléfono (Chrome, Brave, etc.). */
private fun openInBrowser(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (e: android.content.ActivityNotFoundException) {
        // Sin navegador instalado, silenciar
    }
}
