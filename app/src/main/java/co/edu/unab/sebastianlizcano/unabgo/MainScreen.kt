package co.edu.unab.sebastianlizcano.unabgo

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unab.sebastianlizcano.unabgo.LocalAppDimens

@Composable
fun MainScreen(
    navController: NavController,
    onQuieroSerUnabClick: () -> Unit = {},
    onSoyUnabClick: () -> Unit = {},
    onConoceMasClick: () -> Unit = {}
) {
    val dimens = LocalAppDimens.current
    val gradientTop = Color(0xFF460073)
    val gradientBottom = Color(0xFF2F024C)
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val openSansBold = FontFamily(Font(R.font.open_sans_bold))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTop, gradientBottom)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.gapL.dp)
        ) {
            // LOGO
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo UNAB",
                modifier = Modifier
                    .size(dimens.logoSize.dp * 2)
                    .padding(bottom = dimens.gapM.dp),
                contentScale = ContentScale.Fit
            )

            // TÍTULO
            Text(
                text = stringResource(R.string.main_title),
                fontFamily = openSansBold,
                fontSize = dimens.titleXL.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = dimens.gapL.dp)
            )

            // BOTÓN 1 - Quiero ser UNAB
            Button(
                onClick = { navController.navigate("quieroSerUnab") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF490077)
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.buttonHeight.dp)
            ) {
                Text(
                    text = stringResource(R.string.main_button_1),
                    fontFamily = openSans,
                    fontSize = (dimens.titleL * 1.1f).sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            // BOTÓN 2 - Soy UNAB
            Button(
                onClick = { navController.navigate(Routes.SOY_UNAB) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF490077)
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.buttonHeight.dp)
            ) {
                Text(
                    text = stringResource(R.string.main_button_2),
                    fontFamily = openSans,
                    fontSize = (dimens.titleL * 1.1f).sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(dimens.gapL.dp))

            // TEXTO INFERIOR
            Text(
                text = stringResource(R.string.main_button_3),
                fontFamily = openSans,
                fontSize = (dimens.body + 6).sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {

}
