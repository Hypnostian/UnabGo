package co.edu.unab.sebastianlizcano.unabgo.utils

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

/**
 * LocaleManager — Singleton encargado de aplicar y persistir el idioma en SharedPreferences.
 *
 * Importante: el cambio de idioma en una Activity en ejecución requiere recrear la Activity
 * para que los recursos (stringResource) se vuelvan a leer con la nueva Configuration.
 * Eso lo hace SettingsViewModel.setLanguage() llamando a (context as Activity).recreate().
 */
object LocaleManager { // Singleton

    private const val PREF_NAME    = "app_prefs"
    private const val KEY_LANGUAGE = "language"

    /** Guarda el idioma y devuelve un Context con la Configuration ya aplicada. */
    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)
        return updateResources(context, languageCode)
    }

    /** Carga el idioma guardado y devuelve un Context con su Configuration aplicada. */
    fun loadLocale(context: Context, langCode: String? = null): Context {
        val language = langCode ?: getSavedLanguage(context)
        return updateResources(context, language)
    }

    /** Persiste el código de idioma en SharedPreferences. */
    private fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    /** Lee el código de idioma guardado, o "es" por defecto. */
    private fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "es") ?: "es"
    }

    /** Crea un Context con la Configuration aplicada para el locale dado. */
    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    fun getCurrentLanguage(context: Context): String = getSavedLanguage(context)
}
