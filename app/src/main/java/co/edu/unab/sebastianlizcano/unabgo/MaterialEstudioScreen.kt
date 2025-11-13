package co.edu.unab.sebastianlizcano.unabgo

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
import androidx.compose.runtime.LaunchedEffect
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
fun MaterialEstudioScreen(navController: NavController? = null) {

    val context = LocalContext.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    // 🔒 Validación de login
    val user = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(user) {
        if (user == null) {
            navController?.navigate(Routes.LOGIN) {
                popUpTo(Routes.MATERIAL_ESTUDIO) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes = R.string.header_study_material
            )

            Spacer(modifier = Modifier.height(30.dp))

            // LISTA FINAL CON STRINGS + LINKS REALES
            val secciones = listOf(
                Triple(
                    stringResource(R.string.programs_postgrad),
                    "https://drive.google.com/drive/folders/1oltZaUXMWCxRlZDiD2vL1pvYnNMglEo2?usp=sharing",
                    R.drawable.posgradolog
                ),
                Triple(
                    stringResource(R.string.programs_undergrad),
                    "https://drive.google.com/drive/folders/1zEbNMcDUF1ucM4DfrvpFu2SIkjQPrope?usp=sharing",
                    R.drawable.pregradolog
                ),
                Triple(
                    stringResource(R.string.programs_tech),
                    "https://drive.google.com/drive/folders/13Mx9VeVjTuLwkoe1TMC02dS8oCp-V2TF?usp=sharing",
                    R.drawable.progtecytecno
                ),
                Triple(
                    stringResource(R.string.programs_virtual),
                    "https://drive.google.com/drive/folders/1jLOjP45O3kugfLcpSeMaYVmsb52DbEha?usp=sharing",
                    R.drawable.virtualog
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                secciones.forEach { (titulo, link, icono) ->
                    MaterialCard(titulo, link, icono)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Barra inferior
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
fun MaterialCard(
    titulo: String,
    link: String,
    icono: Int
) {
    val context = LocalContext.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF5A237B)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = icono),
                    contentDescription = titulo,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = titulo,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = ">",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
