package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar

@Composable
fun MapaInteractivoScreen(navController: NavController? = null) {

    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    // Popups de lugares
    var showBiblioteca by remember { mutableStateOf(false) }
    var showEdificioL by remember { mutableStateOf(false) }
    var showPlazoleta by remember { mutableStateOf(false) }

    // Popup de "mapa no disponible"
    var showNotAvailable by remember { mutableStateOf(false) }
    var selectedCampus by remember { mutableStateOf("Jardín") }

    val campusOptions = listOf("Jardín", "CSU", "Bosque", "Casona")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = (dimens.buttonHeight * 2f).dp)
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes = R.string.header_map
            )

            Spacer(modifier = Modifier.height(dimens.gapS.dp))

            // ======================
            // SELECTOR DE SEDE
            // ======================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estás viendo la sede $selectedCampus",
                    color = Color.White,
                    fontSize = (dimens.body * 1.15f).sp,
                    fontFamily = openSans
                )

                DropdownMenuCampus(
                    current = selectedCampus,
                    options = campusOptions
                ) { selected ->
                    selectedCampus = selected
                    if (selected != "Jardín") {
                        showNotAvailable = true
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.gapS.dp))

            // ======================
            // MAPA (con posiciones relativas)
            // ======================
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(752f / 583f) // relación aproximada de tu imagen
            ) {
                val mapWidth = maxWidth
                val mapHeight = maxHeight
                val iconSize = (dimens.logoSize * 0.5f).dp

                // Imagen del mapa
                Image(
                    painter = painterResource(id = R.drawable.mapa_unab),
                    contentDescription = stringResource(R.string.map),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // Helper para centrar el icono en la posición (x, y)
                fun Modifier.iconAt(px: Float, py: Float): Modifier {
                    return this
                        .size(iconSize)
                        .offset(
                            x = mapWidth * px - iconSize / 2,
                            y = mapHeight * py - iconSize / 2
                        )
                }

                // ================================
                // Edificio L (L5-1, L7-2, etc.)
                // ================================
                Image(
                    painter = painterResource(id = R.drawable.icono_l),
                    contentDescription = "Edificio L",
                    modifier = Modifier
                        .iconAt(px = 0.26f, py = 0.40f)
                        .clickable { showEdificioL = true }
                )

                // ================================
                // Plazoleta
                // ================================
                Image(
                    painter = painterResource(id = R.drawable.icono_plazoleta),
                    contentDescription = "Plazoleta",
                    modifier = Modifier
                        .iconAt(px = 0.55f, py = 0.33f)
                        .clickable { showPlazoleta = true }
                )

                // ================================
                // Biblioteca
                // ================================
                Image(
                    painter = painterResource(id = R.drawable.icono_biblioteca),
                    contentDescription = stringResource(R.string.library_title),
                    modifier = Modifier
                        .iconAt(px = 0.72f, py = 0.74f)
                        .clickable { showBiblioteca = true }
                )
            }
        }

        // POPUPS DE LUGARES

        if (showBiblioteca) {
            PopupInfo(
                onDismiss = { showBiblioteca = false },
                image = R.drawable.img_biblioteca_popup,
                text = stringResource(R.string.library_text)
            )
        }

        if (showEdificioL) {
            PopupInfo(
                onDismiss = { showEdificioL = false },
                image = R.drawable.img_edificio_l,
                text = "Edificio con aulas, laboratorios y oficinas académicas."
            )
        }

        if (showPlazoleta) {
            PopupInfo(
                onDismiss = { showPlazoleta = false },
                image = R.drawable.img_plazoleta,
                text = "Punto social principal del campus, rodeado de zonas verdes."
            )
        }

        if (showNotAvailable) {
            AlertDialog(
                onDismissRequest = { showNotAvailable = false },
                confirmButton = {
                    Button(onClick = { showNotAvailable = false }) {
                        Text("OK")
                    }
                },
                text = {
                    Text(
                        text = "No disponible el mapa en estos momentos",
                        color = Color.Black,
                        fontFamily = openSans
                    )
                }
            )
        }

        // Bottom Nav
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
fun DropdownMenuCampus(
    current: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = "Cambiar",
            color = Color.White,
            modifier = Modifier
                .background(Color(0x33555555))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}

@Composable
fun PopupInfo(
    onDismiss: () -> Unit,
    image: Int,
    text: String
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = text,
                    color = Color.Black,
                    fontFamily = openSans,
                    fontSize = 15.sp
                )
            }
        }
    )
}
