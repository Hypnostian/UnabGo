package co.edu.unab.sebastianlizcano.unabgo

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@Composable
fun QuieroSerUnabScreen(
    navController: NavController? = null,
    onBackClick: () -> Unit = { navController?.popBackStack("main", inclusive = false) },
    onHomeClick: () -> Unit = { navController?.popBackStack("main", inclusive = false) },
    onBanuClick: () -> Unit = { navController?.navigate("banuia") },
    onProfileClick: () -> Unit = { navController?.navigate("perfil") }
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF490077))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //Encabezado
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

//Button1 en esquina superior izquierda
                Image(
                    painter = painterResource(id = R.drawable.button1),
                    contentDescription = "Decoración encabezado",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 20.dp, top = 20.dp)
                        .size(36.dp) // 🔹 antes 46dp → más pequeño
                )

// Flecha encima de Button1 (corregida y ajustada)
                Image(
                    painter = painterResource(id = R.drawable.flecha),
                    contentDescription = "Volver atrás",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 28.dp, top = 26.dp) // 🔹 más aire
                        .size(20.dp) // 🔹 antes 26dp → más pequeña
                        .clickable { onBackClick() }
                )


                // 🔹 Texto del encabezado
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
                            text = "Explorando",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontFamily = openSans,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
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

            // 🔹 Texto y Banu pensativo (redirige a BanuIA)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onBanuClick() }
            ) {
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF6800AA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¿No sabes todavía qué camino tomar? Banu-IA te puede asesorar. Cuéntale tus sueños y virtudes. ¡Da Click Aquí!",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = openSans,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Image(
                    painter = painterResource(id = R.drawable.banupensativo1),
                    contentDescription = "Banu Pensativo",
                    modifier = Modifier
                        .width(106.dp)
                        .height(119.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            //Tarjetas de programas con íconos
            val secciones = listOf(
                Triple(
                    "Programas Técnicos y Tecnologías",
                    "https://unab.edu.co/programas-tecnicos-y-tecnologias/",
                    R.drawable.progtecytecno
                ),
                Triple(
                    "Programas de Pregrado",
                    "https://unab.edu.co/pregrados/",
                    R.drawable.pregradolog
                ),
                Triple(
                    "Programas de Posgrado",
                    "https://unab.edu.co/posgrado/",
                    R.drawable.posgradolog
                ),
                Triple(
                    "Programas Virtuales",
                    "https://unab.edu.co/programas-virtuales/",
                    R.drawable.virtualog
                ),
                Triple(
                    "Programas de Educación Continua",
                    "https://unab.edu.co/educacion-continua/",
                    R.drawable.educontinualog
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                secciones.forEach { (titulo, link, icono) ->
                    ExpandableWebViewCard(title = titulo, url = link, iconRes = icono)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        // Barra inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(90.dp)
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
                    .padding(horizontal = 35.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // INICIO
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        navController?.popBackStack("main", inclusive = false)
                    }
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

                // BANU-IA
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onBanuClick() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banu),
                        contentDescription = "Banu IA",
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

                // PERFIL
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onProfileClick() }
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
}

//Tarjeta expandible con WebView (scroll independiente)
@Composable
private fun ExpandableWebViewCard(title: String, url: String, iconRes: Int) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)

    val blockNestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF6800AA))
            .clickable { expanded = !expanded }
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "Ícono $title",
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 10.dp)
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .nestedScroll(blockNestedScroll)
            ) {
                AndroidView(factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(url)
                    }
                })
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun QuieroSerUnabPreview() {
    QuieroSerUnabScreen()
}
