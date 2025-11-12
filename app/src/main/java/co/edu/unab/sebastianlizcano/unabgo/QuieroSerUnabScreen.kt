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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar

@Composable
fun QuieroSerUnabScreen(
    navController: NavController? = null
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Usa el HeaderBar global
            HeaderBar(
                navController = navController,
                subtitleRes = R.string.header_exploring
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Texto y Banu pensativo (redirige a BanuIA)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { navController?.navigate("banuia") }
            ) {
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x805A237B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.exploring_text),
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

            // Tarjetas de programas
            val secciones = listOf(
                Triple(
                    stringResource(R.string.programs_tech),
                    "https://unab.edu.co/programas-tecnicos-y-tecnologias/",
                    R.drawable.progtecytecno
                ),
                Triple(
                    stringResource(R.string.programs_undergrad),
                    "https://unab.edu.co/pregrados/",
                    R.drawable.pregradolog
                ),
                Triple(
                    stringResource(R.string.programs_postgrad),
                    "https://unab.edu.co/posgrado/",
                    R.drawable.posgradolog
                ),
                Triple(
                    stringResource(R.string.programs_virtual),
                    "https://unab.edu.co/programas-virtuales/",
                    R.drawable.virtualog
                ),
                Triple(
                    stringResource(R.string.programs_continued),
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

        // Barra inferior fija
        BottomNavBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


// Tarjeta expandible con WebView
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
            .background(Color(0x805A237B))
            .clickable { expanded = !expanded }
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
