package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun PerfilScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val scope = rememberCoroutineScope()

    // DataStore para persistencia
    val dataStore = remember { LanguageDataStore(context) }
    var selectedLang by remember { mutableStateOf("es") }

    // Cargar idioma guardado
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔹 Encabezado con Rectangle6
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rectangle_6),
                    contentDescription = "Encabezado UNAB GO",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // 🔹 Button1 + flecha
                Image(
                    painter = painterResource(id = R.drawable.button1),
                    contentDescription = "Decoración encabezado",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 20.dp, top = 20.dp)
                        .size(36.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.flecha),
                    contentDescription = "Volver atrás",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 26.dp, top = 26.dp)
                        .size(20.dp)
                        .clickable { navController?.popBackStack("main", inclusive = false) }
                )

                // 🔹 Logo y texto
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo UNAB",
                        modifier = Modifier
                            .width(70.dp)
                            .height(70.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "UNAB GO!",
                            style = TextStyle(
                                fontSize = 36.sp,
                                fontFamily = openSans,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 Imagen perfil y Banu
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.perfil),
                    contentDescription = "Perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
                Image(
                    painter = painterResource(id = R.drawable.banuperfil),
                    contentDescription = "Banu Perfil",
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 16.dp, y = (-10).dp)
                )
            }

            // 🔹 Botón de Iniciar sesión
            Button(
                onClick = { navController?.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF490077)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(180.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontFamily = openSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Fondo de opciones
            Box(
                modifier = Modifier
                    .width(402.dp)
                    .height(483.dp)
                    .background(Color(0xCC5A237B))
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth()
                ) {

                    // Idioma selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.translate),
                            contentDescription = "Idioma",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (selectedLang) {
                                "es" -> "Idioma (Español)"
                                "en" -> "Language (English)"
                                "pt" -> "Idioma (Português)"
                                "fr" -> "Langue (Français)"
                                "kr" -> "언어 (한국어)"
                                else -> "Idioma"
                            },
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botones de idioma
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("es", "en", "pt", "fr", "kr").forEach { lang ->
                            Button(
                                onClick = {
                                    scope.launch {
                                        dataStore.saveLanguage(lang)
                                        selectedLang = lang
                                        setLocale(context, lang)
                                        Toast.makeText(context, "Idioma cambiado", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedLang == lang) Color.White else Color.Transparent,
                                    contentColor = if (selectedLang == lang) Color(0xFF490077) else Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = lang.uppercase(),
                                    fontFamily = openSans,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // Calificación
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.favorite),
                            contentDescription = "Califícanos",
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Califícanos", color = Color.White, fontSize = 16.sp)
                    }

                    var rating by remember { mutableStateOf(0) }
                    Row {
                        repeat(5) { index ->
                            Image(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = "Star",
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(2.dp)
                                    .clickable { rating = index + 1 },
                                alpha = if (index < rating) 1f else 0.3f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Otras opciones
                    ProfileOption(R.drawable.notification, "Notificaciones") { }
                    ProfileOption(R.drawable.sug, "Sugerencias") { }
                    ProfileOption(R.drawable.actualiz, "Actualizaciones") { }
                    ProfileOption(R.drawable.polidatos, "Política de datos") { }
                    ProfileOption(R.drawable.credits, "Créditos") { navController?.navigate("creditos") }
                }
            }
        }

        // Barra inferior (igual que las demás pantallas)
        BottomNavBar(navController)
    }
}

@Composable
fun ProfileOption(iconRes: Int, text: String, onClick: () -> Unit) {
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
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = openSans,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(">", color = Color.White, fontSize = 20.sp)
    }
}

fun setLocale(context: Context, langCode: String) {
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocales(LocaleList(locale))
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

@Composable
fun BottomNavBar(navController: NavController?) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color.Transparent)
    ) {
        // Imagen del fondo del nav (Rectangle_5)
        Image(
            painter = painterResource(id = R.drawable.rectangle_5),
            contentDescription = "Barra inferior",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Fila con los tres botones
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 35.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔹 INICIO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController?.navigate("main") }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.botoninicio),
                    contentDescription = "Inicio",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Inicio",
                    fontSize = 16.sp,
                    fontFamily = openSans,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            // 🔹 BANU-IA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController?.navigate("banuia") }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banu),
                    contentDescription = "Banu-IA",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Banu-IA",
                    fontSize = 16.sp,
                    fontFamily = openSans,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            // 🔹 PERFIL
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController?.navigate("perfil") }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.botonperfil),
                    contentDescription = "Perfil",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Perfil",
                    fontSize = 16.sp,
                    fontFamily = openSans,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {
    PerfilScreen()
}
