package co.edu.unab.sebastianlizcano.unabgo.ui.screen

// Separation of Responsibilities — lógica de QR delegada a utils/QrUtils.kt

import co.edu.unab.sebastianlizcano.unabgo.R
import co.edu.unab.sebastianlizcano.unabgo.data.local.CheckingDataStore
import co.edu.unab.sebastianlizcano.unabgo.navigation.Routes
import co.edu.unab.sebastianlizcano.unabgo.utils.loadSavedQR
import co.edu.unab.sebastianlizcano.unabgo.utils.processQRCodeFromUri
import co.edu.unab.sebastianlizcano.unabgo.utils.saveQRBitmap

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.BottomNavBar
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CheckingScreen(navController: NavController? = null) {

    val user = FirebaseAuth.getInstance().currentUser

    // Redirigir si NO hay sesión
    LaunchedEffect(Unit) {
        if (user == null) {
            navController?.navigate(Routes.LOGIN) {
                popUpTo(Routes.CHECKING) { inclusive = true }
            }
        }
    }

    if (user == null) return  // Evita mostrar UI sin sesión

    val context   = LocalContext.current
    val openSans  = FontFamily(Font(R.font.open_sans_regular))
    val dataStore = remember { CheckingDataStore(context) } // DataStore Pattern
    val scope     = rememberCoroutineScope()

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Cargar QR guardado del archivo interno al iniciar
    LaunchedEffect(user.uid) {
        dataStore.getSavedQR(user.uid).collect { path ->
            path?.let { qrBitmap = loadSavedQR(it) } // Utility (QrUtils)
        }
    }

    // Seleccionar imagen desde galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val qr = processQRCodeFromUri(context, it) // Utility (QrUtils)
                if (qr != null) {
                    val savedPath = saveQRBitmap(context, qr, user.uid) // Utility (QrUtils)
                    dataStore.saveQR(user.uid, savedPath)
                    qrBitmap = qr
                }
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
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (qrBitmap == null) {
                Card(
                    modifier = Modifier
                        .size(130.dp)
                        .clickable { galleryLauncher.launch("image/*") },
                    shape  = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xAAFFFFFF))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector     = Icons.Default.Add,
                            contentDescription = "Agregar imagen",
                            tint            = Color(0xFF490077),
                            modifier        = Modifier.size(60.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text       = stringResource(R.string.checking_add_capture),
                    color      = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 18.sp,
                    textAlign  = TextAlign.Center
                )
            } else {
                Image(
                    bitmap             = qrBitmap!!.asImageBitmap(),
                    contentDescription = "QR",
                    modifier           = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentScale       = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(25.dp))
                Button(
                    onClick = {
                        scope.launch {
                            qrBitmap = null
                            dataStore.clearQR(user.uid)
                            File(context.filesDir, "qr_${user.uid}.png").let { if (it.exists()) it.delete() }
                        }
                    },
                    colors   = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF490077)),
                    shape    = RoundedCornerShape(20.dp),
                    modifier = Modifier.width(170.dp).height(45.dp)
                ) {
                    Text("Eliminar QR", fontFamily = openSans, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
            Text(text = user.displayName ?: "", color = Color.White, fontFamily = openSans, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = user.email ?: "",       color = Color.White.copy(alpha = 0.9f), fontFamily = openSans, fontSize = 14.sp)
        }

        HeaderBar(navController = navController, subtitleRes = R.string.header_checking, modifier = Modifier.align(Alignment.TopCenter))
        BottomNavBar(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckingScreenPreview() { CheckingScreen() }
