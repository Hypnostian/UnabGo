package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavController

@Composable
fun PoliticaDatosScreen(navController: NavController? = null) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        // Contenido principal scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 100.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Encabezado con ícono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.polidatos),
                    contentDescription = "Ícono Política de Datos",
                    modifier = Modifier
                        .size(46.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.data_policy_title),
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = openSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            // Tarjeta del texto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.data_policy_content),
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontFamily = openSans,
                            color = Color.White,
                            lineHeight = 26.sp
                        ),
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(Modifier.height(30.dp))
        }

        // Capa superior: botón decorativo + flecha con área táctil amplia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 20.dp)
                .align(Alignment.TopStart)
        ) {

            // Área táctil más grande para asegurar que funcione el click
            Box(
                modifier = Modifier
                    .size(44.dp) // zona táctil amplia
                    .align(Alignment.TopStart)
                    .clickable { navController?.popBackStack() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.flecha),
                    contentDescription = "Volver atrás",
                    modifier = Modifier
                        .padding(start = 8.dp, top = 6.dp)
                        .size(20.dp)
                        .align(Alignment.TopStart)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PoliticaDatosPreview() {
    PoliticaDatosScreen()
}
