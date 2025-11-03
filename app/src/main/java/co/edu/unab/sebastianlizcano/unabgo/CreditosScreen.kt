package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavController

@Composable
fun CreditosScreen(navController: NavController? = null) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo UNAB",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Créditos de desarrollo",
                style = TextStyle(
                    fontSize = 26.sp,
                    color = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Aplicación desarrollada por:\n\nJuan Sebastián Lizcano Jaimes\n Angie Katherine Suarez Ortiz\n\nUniversidad Autónoma de Bucaramanga\n2025",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.White,
                    fontFamily = openSans,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreditosScreenPreview() {
    CreditosScreen()
}
