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
import co.edu.unab.sebastianlizcano.unabgo.LocalAppDimens
import co.edu.unab.sebastianlizcano.unabgo.Routes
import kotlinx.coroutines.launch

@Composable
fun BottomNavBar(
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentLang by remember { mutableStateOf(LocaleManager.getCurrentLanguage(context)) }
    LaunchedEffect(Unit) {
        val dataStore = LanguageDataStore(context)
        dataStore.getLanguage().collect { lang ->
            if (lang != null && lang != currentLang) currentLang = lang
        }
    }

    val iconSize = (dimens.logoSize * 0.45f).dp
    val textSize = (dimens.body + 1).sp
    val barHeight = (dimens.buttonHeight * 1.6f).dp
    val sidePadding = dimens.gapL.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = R.drawable.rectangle_5),
            contentDescription = "Barra inferior",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = sidePadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textStyle = TextStyle(
                fontSize = textSize,
                fontFamily = openSans,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController?.navigate("soyunab") {
                        popUpTo("soyunab") { inclusive = true }
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.botoninicio),
                    contentDescription = stringResource(R.string.bottom_home),
                    modifier = Modifier.size(iconSize)
                )
                Text(text = stringResource(R.string.bottom_home), style = textStyle)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController?.navigate(Routes.BANU_IA) {
                        popUpTo("main") { inclusive = false }
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banu),
                    contentDescription = stringResource(R.string.bottom_banu),
                    modifier = Modifier.size(iconSize)
                )
                Text(text = stringResource(R.string.bottom_banu), style = textStyle)
            }

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
                    modifier = Modifier.size(iconSize)
                )
                Text(text = stringResource(R.string.bottom_profile), style = textStyle)
            }
        }
    }
}
