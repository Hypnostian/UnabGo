package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color

@Composable
fun SplashRoute(
    onFinished: () -> Unit = {},
    minShowTimeMs: Long = 1200L
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(minShowTimeMs)
        onFinished()
    }

    SplashContent(visible = visible)
}

@Composable
private fun SplashContent(visible: Boolean) {
    val gradientTop = Color(0xFF460073)
    val gradientBottom = Color(0xFF2F024C)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTop, gradientBottom),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Image(
                painter = painterResource(id = R.drawable.banu),
                contentDescription = "Logo Banu",
                modifier = Modifier
                    .size(180.dp)
                    .alpha(1f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashPreview() {
    SplashContent(visible = true)
}
