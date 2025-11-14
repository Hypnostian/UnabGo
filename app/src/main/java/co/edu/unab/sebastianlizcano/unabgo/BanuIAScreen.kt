package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel.BanuViewModel

// ------------ Conversión Markdown → Compose ---------------
fun parseMarkdownBold(text: String): AnnotatedString {
    val result = buildAnnotatedString {
        var temp = text
        while (true) {
            val start = temp.indexOf("**")
            if (start == -1) {
                append(temp)
                break
            }
            val end = temp.indexOf("**", start + 2)
            if (end == -1) {
                append(temp)
                break
            }

            // texto antes
            append(temp.substring(0, start))

            // texto en negrita
            val boldText = temp.substring(start + 2, end)
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(boldText)
            }

            temp = temp.substring(end + 2)
        }
    }
    return result
}

// ------------ Modelo para mensaje -------------------------
data class ChatMessage(
    val text: String,
    val fromUser: Boolean
)

@Composable
fun BanuIAScreen(navController: NavController) {

    val dimens = LocalAppDimens.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val vm: BanuViewModel = viewModel()

    val answer by vm.answer.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var userText by remember { mutableStateOf("") }

    val chatMessages = remember { mutableStateListOf<ChatMessage>() }

    LaunchedEffect(answer) {
        answer?.let {
            chatMessages.add(ChatMessage(it, false))
        }
    }

    val scrollState = rememberScrollState()
    LaunchedEffect(chatMessages.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3C005F), Color(0xFF210036))
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = (dimens.buttonHeight * 2.2f).dp)
        ) {

            HeaderBar(navController, subtitleRes = R.string.banu_header)

            Spacer(Modifier.height(4.dp))

            // 🔥 Imagen de Banu más pequeña y arriba
            Image(
                painter = painterResource(id = R.drawable.banusaluda),
                contentDescription = "Banu saludando",
                modifier = Modifier
                    .size(90.dp)   // antes 140dp
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp)
            )

            Text(
                text = stringResource(R.string.banu_help_title),
                color = Color.White,
                fontFamily = openSans,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(6.dp))

            // 🔥 CHAT A PANTALLA COMPLETA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)       // se expande al máximo
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {

                chatMessages.forEach { msg ->
                    ChatBubbleMarkdown(msg.text, msg.fromUser)
                    Spacer(Modifier.height(8.dp))
                }

                if (loading) {
                    ChatBubbleMarkdown("Banu está escribiendo...", false)
                }

                if (error != null) {
                    ChatBubbleMarkdown(error!!, false)
                }
            }

            Spacer(Modifier.height(10.dp))

            // -------- Input y botón enviar -------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = userText,
                    onValueChange = { userText = it },
                    placeholder = { Text(stringResource(R.string.banu_write_here)) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(25.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8A3CDE))
                        .clickable(enabled = !loading && userText.isNotBlank()) {
                            chatMessages.add(ChatMessage(userText, true))
                            vm.askBanu(userText)
                            userText = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                }
            }
        }

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
fun ChatBubbleMarkdown(text: String, fromUser: Boolean) {

    val bubbleColor = if (fromUser) Color(0xFF7D3CFF) else Color(0xFF3B1366)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (fromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = parseMarkdownBold(text),
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}
