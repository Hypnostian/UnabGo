package co.edu.unab.sebastianlizcano.unabgo

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

data class ProgramaItem(
    val title: String,
    val url: String,
    val iconRes: Int
)

@Composable
fun QuieroSerUnabScreen(navController: NavController) {

    val openSans = FontFamily(Font(R.font.open_sans_regular))

    val programas = listOf(
        ProgramaItem(
            stringResource(R.string.programs_tech),
            "https://unab.edu.co/programas-tecnicos-y-tecnologias/",
            R.drawable.progtecytecno
        ),
        ProgramaItem(
            stringResource(R.string.programs_undergrad),
            "https://unab.edu.co/pregrados/",
            R.drawable.pregradolog
        ),
        ProgramaItem(
            stringResource(R.string.programs_postgrad),
            "https://unab.edu.co/posgrado/",
            R.drawable.posgradolog
        ),
        ProgramaItem(
            stringResource(R.string.programs_virtual),
            "https://unab.edu.co/programas-virtuales/",
            R.drawable.virtualog
        ),
        ProgramaItem(
            stringResource(R.string.programs_continued),
            "https://unab.edu.co/educacion-continua/",
            R.drawable.educontinualog
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            HeaderBar(
                navController = navController,
                subtitleRes = R.string.header_exploring
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item { Spacer(modifier = Modifier.height(20.dp)) }

                items(programas) { item ->

                    val encodedUrl = Uri.encode(item.url)
                    val encodedTitle = Uri.encode(item.title)

                    ProgramaCard(item = item, onClick = {
                        navController.navigate(
                            "${Routes.WEBVIEW_DETAIL}?url=$encodedUrl&title=$encodedTitle"
                        )
                    })

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }

        BottomNavBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProgramaCard(item: ProgramaItem, onClick: () -> Unit) {

    val openSans = FontFamily(Font(R.font.open_sans_regular))

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0x805A237B)),
        shape = CardDefaults.shape
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(item.iconRes),
                contentDescription = "Ícono ${item.title}",
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = openSans,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
