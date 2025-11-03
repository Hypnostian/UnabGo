package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class LanguageDataStore(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
    }

    fun getLanguage(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]
    }
}
