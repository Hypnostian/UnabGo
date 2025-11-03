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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    navController: NavController, // ✅ agregado para usar navigate()
    onQuieroSerUnabClick: () -> Unit = {},
    onSoyUnabClick: () -> Unit = {},
    onConoceMasClick: () -> Unit = {}
) {
    val gradientTop = Color(0xFF8700DD)
    val gradientBottom = Color(0xFF490077)
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
                    .padding(horizontal = 32.dp)
            ) {
                // LOGO
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo UNAB",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 22.dp),
                    contentScale = ContentScale.Fit
                )

                // TÍTULO
                Text(
                    text = "UNAB GO!",
                    fontFamily = openSansBold,
                    fontSize = 45.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 38.dp)
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
                        .height(55.dp)
                ) {
                    Text(
                        text = "Quiero ser UNAB",
                        fontFamily = openSans,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(38.dp))

                // BOTÓN 2 - Soy UNAB
                Button(
                    onClick = onSoyUnabClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF490077)
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Text(
                        text = "Soy UNAB",
                        fontFamily = openSans,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(38.dp))

                // TEXTO INFERIOR
                Text(
                    text = "Conoce más sobre la Unab",
                    fontFamily = openSans,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun MainScreenPreview() {

    }
