package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.LocaleList
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun PerfilScreen(navController: NavController? = null) {
    val dimens = LocalAppDimens.current
    val context = LocalContext.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val scope = rememberCoroutineScope()
    val dataStore = remember { LanguageDataStore(context) }
    var selectedLang by remember { mutableStateOf("es") }

    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        dataStore.getLanguage().collect { lang ->
            selectedLang = lang ?: "es"
            setLocale(context, selectedLang)
        }
    }

    //Scroll principal para todos los contenidos
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = (dimens.buttonHeight * 2f).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderBar(navController = navController)

            // Imagen de perfil + Banu
            Box(
                modifier = Modifier
                    .padding(top = dimens.gapS.dp)
                    .height((dimens.heroImageSize * 0.8f).dp),
                contentAlignment = Alignment.Center
            ) {
                if (user?.photoUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(user.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size((dimens.logoSize * 1.3f).dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.perfil),
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size((dimens.logoSize * 1.5f).dp)
                            .clip(CircleShape)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.banuperfil),
                    contentDescription = "Banu Perfil",
                    modifier = Modifier
                        .size((dimens.logoSize * 0.8f).dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-dimens.gapM).dp, y = (-dimens.gapS).dp)
                )
            }

            Spacer(Modifier.height(dimens.gapS.dp))

            if (user != null) {
                Text(
                    text = user.displayName ?: "Usuario UNAB",
                    color = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = (dimens.titleL * 0.9f).sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = user.email ?: "",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = openSans,
                    fontSize = (dimens.body * 0.95f).sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(dimens.gapM.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(
                            context,
                            context.getString(R.string.profile_session_closed),
                            Toast.LENGTH_SHORT
                        ).show()
                        navController?.navigate(Routes.LOGIN) {
                            popUpTo(Routes.PERFIL) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width((dimens.logoSize * 1.6f).dp)
                        .height((dimens.buttonHeight * 0.8f).dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_logout),
                        fontFamily = openSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = (dimens.body * 0.8f).sp
                    )
                }

                Spacer(Modifier.height(dimens.gapM.dp))
            } else {
                Button(
                    onClick = { navController?.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF490077)
                    ),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .width((dimens.logoSize * 1.8f).dp)
                        .height((dimens.buttonHeight * 0.85f).dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_login_button),
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (dimens.body * 1.2f).sp
                    )
                }
                Spacer(Modifier.height(dimens.gapM.dp))
            }

            //Panel de opciones scrolleable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCC5A237B))
                    .padding(horizontal = dimens.gapM.dp, vertical = dimens.gapS.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.translate),
                        contentDescription = "Idioma",
                        modifier = Modifier.size((dimens.logoSize * 0.35f).dp)
                    )
                    Spacer(Modifier.width(dimens.gapS.dp))
                    Text(
                        text = when (selectedLang) {
                            "es" -> "Idioma (Español)"
                            "en" -> "Language (English)"
                            "pt" -> "Idioma (Português)"
                            "fr" -> "Langue (Français)"
                            "ko" -> "언어 (한국어)"
                            else -> "Idioma"
                        },
                        color = Color.White,
                        fontFamily = openSans,
                        fontSize = (dimens.body * 1.1f).sp
                    )
                }

                Spacer(Modifier.height(dimens.gapS.dp))

                //Idiomas en fila horizontal (siempre bien alineados)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.gapS.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("ES", "EN", "PT", "FR", "KO").forEach { lang ->
                        val code = lang.lowercase(Locale.getDefault())
                        Button(
                            onClick = {
                                scope.launch {
                                    dataStore.saveLanguage(code)
                                    selectedLang = code
                                    setLocale(context, code)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.language_changed),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLang == code) Color.White else Color.Transparent,
                                contentColor = if (selectedLang == code) Color(0xFF490077) else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = lang,
                                fontFamily = openSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = (dimens.body * 1f).sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(dimens.gapM.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.favorite),
                        contentDescription = "Califícanos",
                        modifier = Modifier.size((dimens.logoSize * 0.3f).dp)
                    )
                    Spacer(Modifier.width(dimens.gapS.dp))
                    Text(
                        text = stringResource(R.string.profile_rate_us),
                        color = Color.White,
                        fontFamily = openSans,
                        fontSize = (dimens.body * 1.1f).sp
                    )
                }

                var rating by remember { mutableStateOf(0) }
                Row {
                    repeat(5) { i ->
                        Image(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "star",
                            modifier = Modifier
                                .size((dimens.logoSize * 0.33f).dp)
                                .padding(2.dp)
                                .clickable { rating = i + 1 },
                            alpha = if (i < rating) 1f else 0.3f
                        )
                    }
                }

                Spacer(Modifier.height(dimens.gapS.dp))

                ProfileOption(R.drawable.notification, stringResource(R.string.profile_notifications)) {
                    val intent = Intent("android.settings.APP_NOTIFICATION_SETTINGS").apply {
                        putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                    }
                    context.startActivity(intent)
                }

                ProfileOption(R.drawable.sug, stringResource(R.string.profile_suggestions)) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://forms.gle/coH1nshxCG9HDBJw6")
                    )
                    context.startActivity(intent)
                }

                ProfileOption(
                    R.drawable.actualiz,
                    stringResource(R.string.profile_updates)
                ) { navController?.navigate(Routes.ACTUALIZACIONES) }

                ProfileOption(
                    R.drawable.polidatos,
                    stringResource(R.string.profile_data_policy)
                ) { navController?.navigate(Routes.POLITICADATOS) }

                ProfileOption(R.drawable.credits, stringResource(R.string.profile_credits)) {
                    navController?.navigate(Routes.CREDITOS)
                }
            }
        }

        // Barra inferior fija
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
private fun ProfileOption(iconRes: Int, text: String, onClick: () -> Unit) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimens.gapS.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size((dimens.logoSize * 0.3f).dp)
        )
        Spacer(Modifier.width(dimens.gapS.dp))
        Text(
            text,
            color = Color.White,
            fontFamily = openSans,
            fontSize = (dimens.body * 1.1f).sp
        )
        Spacer(Modifier.weight(1f))
        Text(">", color = Color.White, fontSize = (dimens.titleL * 0.9f).sp)
    }
}

fun setLocale(context: Context, langCode: String) {
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocales(LocaleList(locale))
    context.createConfigurationContext(config)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
