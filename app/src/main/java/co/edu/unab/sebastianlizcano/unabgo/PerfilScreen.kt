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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderBar(navController = navController)

            // Imagen de perfil + Banu
            Box(
                modifier = Modifier
                    .padding(top = 0.dp)
                    .height(160.dp),
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
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.perfil),
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.banuperfil),
                    contentDescription = "Banu Perfil",
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-15).dp, y = (-20).dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            if (user != null) {
                // Nombre + correo
                Text(
                    text = user.displayName ?: "Usuario UNAB",
                    color = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = user.email ?: "",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = openSans,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                // Botón Cerrar sesión
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
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
                        .width(150.dp)
                        .height(42.dp)
                ) {
                    Text(
                        text = "Cerrar sesión",
                        fontFamily = openSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.height(20.dp))
            } else {
                // Botón iniciar sesión
                Button(
                    onClick = { navController?.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF490077)
                    ),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .width(165.dp)
                        .height(45.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_login_button),
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            // Panel de opciones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCC5A237B))
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .weight(1f, fill = true),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.translate),
                        contentDescription = "Idioma",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
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
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.favorite),
                        contentDescription = "Califícanos",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.profile_rate_us),
                        color = Color.White,
                        fontFamily = openSans,
                        fontSize = 16.sp
                    )
                }

                var rating by remember { mutableStateOf(0) }
                Row {
                    repeat(5) { i ->
                        Image(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "star",
                            modifier = Modifier
                                .size(30.dp)
                                .padding(2.dp)
                                .clickable { rating = i + 1 },
                            alpha = if (i < rating) 1f else 0.3f
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

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
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.White, fontFamily = openSans, fontSize = 16.sp)
        Spacer(Modifier.weight(1f))
        Text(">", color = Color.White, fontSize = 18.sp)
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
