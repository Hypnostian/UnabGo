package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.NewsDetailState
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.NewsDetailViewModel

@Composable
fun NewsDetailScreen(
    navController: NavController,
    postId: Long,
    viewModel: NewsDetailViewModel = viewModel()
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val context  = LocalContext.current

    LaunchedEffect(postId) { viewModel.load(postId) }
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes   = R.string.announcements
            )

            when (val current = state) {
                is NewsDetailState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8E5BFF))
                    }
                }
                is NewsDetailState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.banupensativo1),
                            contentDescription = null,
                            modifier = Modifier.size(110.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text       = current.message,
                            color      = Color.White,
                            fontFamily = openSans,
                            fontSize   = 14.sp,
                            textAlign  = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.load(postId) },
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8E5BFF),
                                contentColor   = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reintentar", fontFamily = openSans, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                is NewsDetailState.Success -> {
                    val item = current.item

                    // Imagen hero
                    if (item.imageUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text       = item.title,
                            color      = Color.White,
                            fontFamily = openSans,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 22.sp,
                            lineHeight = 28.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text       = item.date,
                            color      = Color(0xFFC9B6FF),
                            fontFamily = openSans,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(16.dp))

                        // Cuerpo HTML renderizado con TextView nativo
                        HtmlText(
                            html     = item.contentHtml,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8E5BFF),
                                contentColor   = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text       = "Abrir en navegador",
                                fontFamily = openSans,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                setTextColor(android.graphics.Color.WHITE)
                textSize = 15f
                setLineSpacing(6f, 1.1f)
                setLinkTextColor(android.graphics.Color.parseColor("#C9B6FF"))
                movementMethod = android.text.method.LinkMovementMethod.getInstance()
            }
        },
        update = { tv ->
            tv.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        },
        modifier = modifier
    )
}
