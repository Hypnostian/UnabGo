package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.LocalAppDimens
import co.edu.unab.sebastianlizcano.unabgo.navigation.Routes

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SoyUnabScreen(navController: NavController? = null) {
    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = (dimens.buttonHeight * 2f).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderBar(navController = navController)

            Spacer(modifier = Modifier.height(dimens.gapS.dp))
            Spacer(modifier = Modifier.height(dimens.gapM.dp))

            // Lista de módulos (con direcciones internas y externas)
            val modulos = listOf(
                Triple(R.drawable.carnet, R.string.card_id, "app:carnet"),
                Triple(R.drawable.checking, R.string.checking, "nav:checking"),
                Triple(R.drawable.avisos, R.string.announcements, "nav:avisos"),
                Triple(R.drawable.docentes, R.string.teachers, "nav:docentes"),
                Triple(R.drawable.m_estudio, R.string.study_material, "nav:materialestudio"),
                Triple(R.drawable.calculadora, R.string.calculator, "nav:calculadora"),
                Triple(R.drawable.tema, R.string.tema, "https://tema.unab.edu.co/"),
                Triple(R.drawable.cosmos, R.string.cosmos, "https://cosmos.unab.edu.co/"),
                Triple(R.drawable.miportalu, R.string.mi_portal_u, "https://miportalu.unab.edu.co/"),
                Triple(R.drawable.biblioteca, R.string.library_title, "https://catalogo.unab.edu.co/"),
                Triple(R.drawable.horario, R.string.schedule, "nav:horario"),
                Triple(R.drawable.mapaunab, R.string.map, "nav:mapa")
            )

            // Grid de botones (3 por fila)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.gapL.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in modulos.chunked(3)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row.forEach { (icono, texto, destino) ->
                            SoyUnabButton(
                                icono = icono,
                                texto = stringResource(texto),
                                font = openSans,
                                destino = destino,
                                navController = navController,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        horizontal = dimens.gapS.dp,
                                        vertical = dimens.gapM.dp * 0.8f
                                    )
                            )
                        }
                        repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }

        // Barra inferior fija
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun SoyUnabButton(
    icono: Int,
    texto: String,
    font: FontFamily,
    destino: String,
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    val dimens = LocalAppDimens.current
    val context = LocalContext.current

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable {
                when {
                    destino.startsWith("https://") -> {
                        // Enlace externo web
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(destino))
                        context.startActivity(intent)
                    }

                    destino.startsWith("app:carnet") -> {

                        // Verificar si hay usuario logueado
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser == null) {
                            navController?.navigate(Routes.LOGIN)
                            return@clickable
                        }

                        // Lista de posibles package names para la app de carnet UNAB
                        // (Veridda VECard puede tener varios nombres según la versión).
                        // Estos package names DEBEN estar declarados en <queries> del Manifest
                        // para que Android 11+ permita verificar si están instalados.
                        val candidatePackages = listOf(
                            "com.veriddica.vecard",
                            "co.edu.unab.vecard",
                            "co.edu.unab.carnet"
                        )

                        // Buscar la primera app instalada
                        val launchIntent = candidatePackages
                            .firstNotNullOfOrNull { pkg ->
                                context.packageManager.getLaunchIntentForPackage(pkg)
                            }

                        if (launchIntent != null) {
                            // App instalada -> abrirla
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(launchIntent)
                        } else {
                            // No instalada -> intentar Play Store (primero con market://, luego https)
                            val playPackage = candidatePackages.first()
                            val marketUri = Uri.parse("market://details?id=$playPackage")
                            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                context.startActivity(marketIntent)
                            } catch (e: ActivityNotFoundException) {
                                val webUri = Uri.parse(
                                    "https://play.google.com/store/apps/details?id=$playPackage"
                                )
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, webUri)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        }
                    }


                    destino.startsWith("nav:") -> {
                        // Navegación interna
                        val route = destino.removePrefix("nav:")
                        navController?.navigate(route)
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0x805A237B)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.gapS.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icono),
                contentDescription = texto,
                modifier = Modifier
                    .size((dimens.logoSize * 0.6f).dp)
                    .padding(bottom = dimens.gapS.dp)
            )
            Text(
                text = texto,
                fontFamily = font,
                color = Color.White,
                fontSize = (dimens.body * 1.1f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
