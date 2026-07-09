package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import co.edu.unab.sebastianlizcano.unabgo.data.remote.NewsItem
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.LocalAppDimens
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.NewsUiState
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.NewsViewModel
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.UnabCategoryIds

/**
 * Pantalla de noticias 100% nativa (sin WebView, sin rotate-warning).
 *  - Consume la WordPress REST API de unab.edu.co
 *  - Lista de cards en LazyColumn con imagen + título + fecha + excerpt
 *  - Chips horizontales para filtrar por categoría
 *  - Toca una noticia -> navega a NewsDetailScreen
 */
@Composable
fun AvisosScreen(
    navController: NavController? = null,
    viewModel: NewsViewModel = viewModel()
) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val dimens   = LocalAppDimens.current

    val state            by viewModel.state.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories = listOf(
        CategoryFilter(stringResource(R.string.category_all),           UnabCategoryIds.ALL),
        CategoryFilter(stringResource(R.string.category_institucional), UnabCategoryIds.ACTUALIDAD_INSTITUCIONAL),
        CategoryFilter(stringResource(R.string.category_investigacion), UnabCategoryIds.INVESTIGACION),
        CategoryFilter(stringResource(R.string.category_cultura),       UnabCategoryIds.ARTE_CULTURA),
        CategoryFilter(stringResource(R.string.category_impacto),       UnabCategoryIds.HISTORIAS_CON_IMPACTO)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = (dimens.buttonHeight * 1.8f).dp)
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes   = R.string.announcements
            )

            Spacer(Modifier.height(12.dp))

            // Chips de categoría (scroll horizontal: ESTOS chips no causan
            // problema porque NO estamos cargando una página web con CSS
            // responsive, es Compose nativo).
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    CategoryChip(
                        label    = cat.label,
                        selected = selectedCategory == cat.id,
                        onClick  = { viewModel.selectCategory(cat.id) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Contenido segun estado
            when (val current = state) {
                is NewsUiState.Loading -> LoadingView()
                is NewsUiState.Error   -> ErrorView(
                    message  = current.message,
                    openSans = openSans,
                    onRetry  = { viewModel.load() }
                )
                is NewsUiState.Success -> {
                    if (current.items.isEmpty()) {
                        EmptyView(openSans)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(current.items, key = { it.id }) { item ->
                                NewsCard(
                                    item     = item,
                                    openSans = openSans,
                                    onClick  = {
                                        navController?.navigate("newsDetail/${item.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            BottomNavBar(navController)
        }
    }
}

// =============================================================
// Componentes
// =============================================================

private data class CategoryFilter(val label: String, val id: Int?)

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    Surface(
        shape    = RoundedCornerShape(20.dp),
        color    = if (selected) Color(0xFF8E5BFF) else Color.White.copy(alpha = 0.12f),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text       = label,
            color      = Color.White,
            fontFamily = openSans,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 13.sp,
            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun NewsCard(
    item: NewsItem,
    openSans: FontFamily,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A105D))
    ) {
        Column {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text       = item.title,
                    color      = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    maxLines   = 3
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text       = item.date,
                    color      = Color(0xFFC9B6FF),
                    fontFamily = openSans,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.excerpt.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text       = item.excerpt,
                        color      = Color.White.copy(alpha = 0.85f),
                        fontFamily = openSans,
                        fontSize   = 13.sp,
                        maxLines   = 3
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF8E5BFF))
    }
}

@Composable
private fun ErrorView(message: String, openSans: FontFamily, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.banupensativo1),
            contentDescription = null,
            modifier = Modifier.size(110.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text       = message,
            color      = Color.White,
            fontFamily = openSans,
            fontSize   = 14.sp,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
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

@Composable
private fun EmptyView(openSans: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = "No hay noticias en esta categoría",
            color      = Color.White,
            fontFamily = openSans,
            fontSize   = 14.sp,
            textAlign  = TextAlign.Center
        )
    }
}
