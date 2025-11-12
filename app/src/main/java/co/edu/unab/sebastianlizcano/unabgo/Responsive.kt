package co.edu.unab.sebastianlizcano.unabgo

import androidx.compose.runtime.staticCompositionLocalOf

data class AppDimens(
    val titleXL: Float,
    val titleL: Float,
    val body: Float,
    val buttonHeight: Int,
    val logoSize: Int,
    val heroImageSize: Int,
    val gapS: Int,
    val gapM: Int,
    val gapL: Int
)

// Valores por defecto (se sobreescriben en MainActivity)
val LocalAppDimens = staticCompositionLocalOf {
    AppDimens(
        titleXL = 45f,
        titleL = 22f,
        body = 14f,
        buttonHeight = 55,
        logoSize = 90,
        heroImageSize = 210,
        gapS = 8,
        gapM = 16,
        gapL = 32
    )
}
