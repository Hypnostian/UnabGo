package co.edu.unab.sebastianlizcano.unabgo.utils

// Utility: fuerza la orientación de la Activity mientras el Composable esté activo.
// Useful para pantallas con WebView u otras que solo se ven bien en portrait.

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Bloquea la orientación de la Activity en portrait mientras el Composable está en pantalla.
 * Restaura la orientación previa al salir.
 *
 * Uso:
 * ```
 * @Composable
 * fun MiPantalla() {
 *     LockOrientationPortrait()
 *     ...
 * }
 * ```
 */
@Composable
fun LockOrientationPortrait() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val original = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            activity?.requestedOrientation =
                original ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

/** Encuentra la Activity desde un Context anidado (típico en Compose). */
fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
