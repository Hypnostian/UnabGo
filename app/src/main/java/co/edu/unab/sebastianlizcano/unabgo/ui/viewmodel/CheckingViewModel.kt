package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — gestiona el estado de la pantalla de carnet (Checking)
// Separation of Responsibilities — aísla la lógica de DataStore y QR del Composable

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.sebastianlizcano.unabgo.data.local.CheckingDataStore
import co.edu.unab.sebastianlizcano.unabgo.utils.loadSavedQR
import co.edu.unab.sebastianlizcano.unabgo.utils.processQRCodeFromUri
import co.edu.unab.sebastianlizcano.unabgo.utils.saveQRBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class CheckingUiState(
    val qrBitmap: Bitmap? = null,
    val isLoading: Boolean = false
)

class CheckingViewModel : ViewModel() { // ViewModel (MVVM)

    // Observer Pattern (StateFlow)
    private val _uiState = MutableStateFlow(CheckingUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Carga el QR guardado en almacenamiento interno al iniciar la pantalla.
     */
    fun loadSavedQrCode(context: Context, userId: String) {
        viewModelScope.launch {
            val dataStore = CheckingDataStore(context) // DataStore Pattern
            dataStore.getSavedQR(userId).collect { path ->
                path?.let {
                    val bitmap = loadSavedQR(it) // Utility (QrUtils)
                    _uiState.value = _uiState.value.copy(qrBitmap = bitmap)
                }
            }
        }
    }

    /**
     * Procesa la imagen seleccionada desde la galería, detecta el QR y lo guarda.
     */
    fun processAndSaveQr(context: Context, uri: Uri, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val qr = processQRCodeFromUri(context, uri) // Utility (QrUtils)
            if (qr != null) {
                val savedPath = saveQRBitmap(context, qr, userId) // Utility (QrUtils)
                val dataStore = CheckingDataStore(context)
                dataStore.saveQR(userId, savedPath)
                _uiState.value = _uiState.value.copy(qrBitmap = qr, isLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Elimina el QR actual del estado y del almacenamiento.
     */
    fun deleteQr(context: Context, userId: String) {
        viewModelScope.launch {
            val dataStore = CheckingDataStore(context)
            dataStore.clearQR(userId)
            File(context.filesDir, "qr_$userId.png").let { if (it.exists()) it.delete() }
            _uiState.value = _uiState.value.copy(qrBitmap = null)
        }
    }
}
