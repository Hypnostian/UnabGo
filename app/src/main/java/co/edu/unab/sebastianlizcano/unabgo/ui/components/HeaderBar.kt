package co.edu.unab.sebastianlizcano.unabgo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun HeaderBar(
    navController: NavController? = null,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = { navController?.popBackStack("main", inclusive = false) }
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        // Fondo de encabezado
        Image(
            painter = painterResource(id = R.drawable.rectangle_6),
            contentDescription = "Encabezado UNAB GO",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Botón decorativo
        Image(
            painter = painterResource(id = R.drawable.button1),
            contentDescription = "Decoración encabezado",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 20.dp)
                .size(36.dp)
        )

        // Flecha atrás
        Image(
            painter = painterResource(id = R.drawable.flecha),
            contentDescription = "Volver atrás",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 28.dp, top = 26.dp)
                .size(20.dp)
                .clickable { onBackClick() }
        )

        // Logo y título principal
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

            Text(
                text = stringResource(R.string.main_title),
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
