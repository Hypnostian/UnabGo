package co.edu.unab.sebastianlizcano.unabgo.utils

// Separation of Responsibilities — utilidades de QR extraídas de CheckingScreen
// Utility / Helper — funciones puras sin estado para procesamiento de imágenes QR

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Recorta el área del QR de forma segura, evitando crashes por coordenadas fuera de rango. */
fun safeCrop(bitmap: Bitmap, box: Rect): Bitmap? {
    val x      = box.left.coerceIn(0, bitmap.width - 1)
    val y      = box.top.coerceIn(0, bitmap.height - 1)
    val width  = box.width().coerceAtMost(bitmap.width - x)
    val height = box.height().coerceAtMost(bitmap.height - y)
    if (width <= 0 || height <= 0) return null
    return try {
        Bitmap.createBitmap(bitmap, x, y, width, height)
    } catch (e: Exception) {
        null
    }
}

/** Carga la URI, detecta el QR con ML Kit y retorna el bitmap recortado. */
suspend fun processQRCodeFromUri(context: Context, uri: Uri): Bitmap? =
    withContext(Dispatchers.IO) {
        try {
            val bitmap   = loadBitmapFromUri(context, uri)
            val image    = InputImage.fromBitmap(bitmap, 0)
            val barcodes = BarcodeScanning.getClient().process(image).await()
            val box      = barcodes.firstOrNull()?.boundingBox ?: return@withContext null
            safeCrop(bitmap, box)
        } catch (e: Exception) {
            null
        }
    }

/** Guarda el bitmap del QR en almacenamiento interno y retorna la ruta. */
fun saveQRBitmap(context: Context, bitmap: Bitmap, uid: String): String {
    val file = File(context.filesDir, "qr_${uid}.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return file.absolutePath
}

/** Carga un bitmap desde disco por ruta absoluta. */
fun loadSavedQR(path: String): Bitmap? = try {
    BitmapFactory.decodeFile(path)
} catch (e: Exception) {
    null
}

/** Decodifica un URI como Bitmap, compatible con API 28+. */
fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }

/** Extension Function — convierte una Task de Firebase en coroutine suspendible. */
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it, null) }
        addOnFailureListener { cont.resumeWithException(it) }
    }
