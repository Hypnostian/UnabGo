package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.checkingDataStore by preferencesDataStore("checking_prefs")

class CheckingDataStore(private val context: Context) {

    private fun keyForUser(userId: String): Preferences.Key<String> =
        stringPreferencesKey("qr_path_$userId")

    fun getSavedQR(userId: String): Flow<String?> {
        return context.checkingDataStore.data.map { prefs ->
            prefs[keyForUser(userId)]
        }
    }

    suspend fun saveQR(userId: String, path: String) {
        context.checkingDataStore.edit { prefs ->
            prefs[keyForUser(userId)] = path
        }
    }

    suspend fun clearQR(userId: String) {
        context.checkingDataStore.edit { prefs ->
            prefs.remove(keyForUser(userId))
        }
    }
}
