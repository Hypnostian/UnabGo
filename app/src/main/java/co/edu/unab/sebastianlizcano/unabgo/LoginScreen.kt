package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    navController: NavController? = null,
    onBackClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    val purpleTop = Color(0xFF8700DD)
    val purpleBottom = Color(0xFF490077)
    val purpleText = Color(0xFF490077)

    val openSans = FontFamily(
        Font(R.font.open_sans_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(purpleTop, purpleBottom)))
    ) {
        // Flecha atrás
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        // Contenido centrado verticalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo + texto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo UNAB",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.main_title),
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(120.dp))

            // Búho + campo Usuario
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PillField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = stringResource(R.string.login_user),
                    textStyle = TextStyle(
                        fontFamily = openSans,
                        fontSize = 24.sp,
                        color = purpleText,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.medbanu),
                    contentDescription = "Búho Banu",
                    modifier = Modifier
                        .size(180.dp)
                        .offset(y = (-30).dp),
                    contentScale = ContentScale.Fit
                )
            }

            //  Espaciado  entre user - contraseña
            Spacer(Modifier.height(1.dp))

            PillField(
                value = password,
                onValueChange = { password = it },
                placeholder = stringResource(R.string.login_password),
                textStyle = TextStyle(
                    fontFamily = openSans,
                    fontSize = 24.sp,
                    color = purpleText,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.login_forgot_password),
                fontFamily = openSans,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.clickable { onForgotPassword() }
            )
        }
    }
}

// Campo de texto tipo píldora centrado
@Composable
private fun PillField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(58.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle,
            decorationBox = { inner ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = textStyle)
                    }
                    inner()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}
