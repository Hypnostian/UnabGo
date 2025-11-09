package co.edu.unab.sebastianlizcano.unabgo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.LanguageDataStore
import co.edu.unab.sebastianlizcano.unabgo.LocaleManager
import kotlinx.coroutines.launch

@Composable
fun BottomNavBar(
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Escucha el idioma actual guardado y fuerza recomposición cuando cambia
    var currentLang by remember { mutableStateOf(LocaleManager.getCurrentLanguage(context)) }
    LaunchedEffect(Unit) {
        val dataStore = LanguageDataStore(context)
        dataStore.getLanguage().collect { lang ->
            if (lang != null && lang != currentLang) {
                currentLang = lang
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color.Transparent)
    ) {
        // Fondo de la barra inferior
        Image(
            painter = painterResource(id = R.drawable.rectangle_5),
            contentDescription = "Barra inferior",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Contenedor principal con los tres botones
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 35.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = openSans,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            // Botón de Inicio
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController?.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.botoninicio),
                    contentDescription = stringResource(R.string.bottom_home),
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(R.string.bottom_home),
                    style = textStyle
                )
            }

            // Botón de Banu-IA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController?.navigate("banuia") {
                        popUpTo("main") { inclusive = false }
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banu),
                    contentDescription = stringResource(R.string.bottom_banu),
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(R.string.bottom_banu),
                    style = textStyle
                )
            }

            // Botón de Perfil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController?.navigate("perfil") {
                        launchSingleTop = true
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.botonperfil),
                    contentDescription = stringResource(R.string.bottom_profile),
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(R.string.bottom_profile),
                    style = textStyle
                )
            }
        }
    }
}
