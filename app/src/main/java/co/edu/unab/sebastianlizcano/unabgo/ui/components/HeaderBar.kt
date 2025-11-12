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
import co.edu.unab.sebastianlizcano.unabgo.LocalAppDimens

@Composable
fun HeaderBar(
    navController: NavController? = null,
    modifier: Modifier = Modifier,
    subtitleRes: Int = R.string.header_exploring,
    onBackClick: () -> Unit = { navController?.popBackStack() }

) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    val headerHeight = (dimens.heroImageSize * 0.65f).dp
    val logoSize = (dimens.logoSize * 0.78f).dp
    val subtitleSize = (dimens.titleL * 0.9f).sp
    val titleSize = dimens.titleXL.sp
    val paddingSide = dimens.gapM.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.rectangle_6),
            contentDescription = "Encabezado UNAB GO",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Flecha atrás funcional
        Image(
            painter = painterResource(id = R.drawable.flecha),
            contentDescription = "Volver atrás",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = (dimens.gapM * 1.4f).dp, top = (dimens.gapM * 1.3f).dp)
                .size((dimens.gapM * 1.5f).dp)
                .clickable { onBackClick() }
        )

        // Contenido central
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = paddingSide),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo UNAB",
                modifier = Modifier
                    .size(logoSize),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(dimens.gapS.dp))

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = stringResource(subtitleRes).uppercase(),
                    style = TextStyle(
                        fontSize = subtitleSize,
                        fontFamily = openSans,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )

                Text(
                    text = "UNAB GO!",
                    style = TextStyle(
                        fontSize = titleSize,
                        fontFamily = openSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
