package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.graphics.*
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    val context = LocalContext.current
    val openSans = FontFamily(Font(R.font.open_sans_regular))
    val dataStore = remember { CheckingDataStore(context) }

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()

    //  Cargar QR guardado del archivo interno
    LaunchedEffect(user.uid) {
        dataStore.getSavedQR(user.uid).collect { path ->
            path?.let {
                qrBitmap = loadSavedQR(it)
            }
        }
    }

    // Seleccionar imagen desde galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val qr = processQRCodeFromUri(context, it)
                if (qr != null) {

                    // Guardar QR recortado como archivo interno
                    val savedPath = saveQRBitmap(context, qr, user.uid)

                    // Guardar referencia en DataStore
                    dataStore.saveQR(user.uid, savedPath)

                    // Mostrar en pantalla
                    qrBitmap = qr
                }
            }
        }
    }

    // --------------------
    // UI
    // --------------------
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

            // ➕ Si NO hay QR → mostrar botón
            if (qrBitmap == null) {
                Card(
                    modifier = Modifier
                        .size(130.dp)
                        .clickable { galleryLauncher.launch("image/*") },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color(0xAAFFFFFF))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar imagen",
                            tint = Color(0xFF490077),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.checking_add_capture),
                    color = Color.White,
                    fontFamily = openSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 🟩 Mostrar QR guardado
            else {
                Image(
                    bitmap = qrBitmap!!.asImageBitmap(),
                    contentDescription = "QR",
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Eliminar QR
                Button(
                    onClick = {
                        scope.launch {
                            qrBitmap = null
                            dataStore.clearQR(user.uid)

                            // eliminar archivo
                            val file = File(context.filesDir, "qr_${user.uid}.png")
                            if (file.exists()) file.delete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF490077)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.width(170.dp).height(45.dp)
                ) {
                    Text("Eliminar QR", fontFamily = openSans, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // 👤 Datos del usuario
            Text(
                text = user.displayName ?: "",
                color = Color.White,
                fontFamily = openSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = user.email ?: "",
                color = Color.White.copy(alpha = 0.9f),
                fontFamily = openSans,
                fontSize = 14.sp
            )
        }

        HeaderBar(
            navController = navController,
            subtitleRes = R.string.header_checking,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        BottomNavBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

//
// MLKit + utilidades
//

// Evita crashes por coordenadas fuera de rango
fun safeCrop(bitmap: Bitmap, box: Rect): Bitmap? {
    val x = box.left.coerceIn(0, bitmap.width - 1)
    val y = box.top.coerceIn(0, bitmap.height - 1)
    val width = box.width().coerceAtMost(bitmap.width - x)
    val height = box.height().coerceAtMost(bitmap.height - y)

    if (width <= 0 || height <= 0) return null

    return try {
        Bitmap.createBitmap(bitmap, x, y, width, height)
    } catch (e: Exception) {
        null
    }
}

suspend fun processQRCodeFromUri(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val bitmap = loadBitmapFromUri(context, uri)
            val image = InputImage.fromBitmap(bitmap, 0)
            val barcodes = BarcodeScanning.getClient().process(image).await()

            if (barcodes.isNotEmpty()) {
                val box = barcodes.first().boundingBox ?: return@withContext null
                return@withContext safeCrop(bitmap, box)
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}

fun saveQRBitmap(context: Context, bitmap: Bitmap, uid: String): String {
    val file = File(context.filesDir, "qr_${uid}.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return file.absolutePath
}

fun loadSavedQR(path: String): Bitmap? {
    return try {
        BitmapFactory.decodeFile(path)
    } catch (e: Exception) {
        null
    }
}

fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= 28) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}

suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it, null) }
        addOnFailureListener { cont.resumeWithException(it) }
    }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckingScreenPreview() {
    CheckingScreen()
}
