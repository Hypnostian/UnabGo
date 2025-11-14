package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import java.util.*

object LocaleManager {
    private const val PREF_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "language"

    //  Permite aplicar idioma directamente (usado por PerfilScreen)
    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)
        return updateResources(context, languageCode)
    }

    //  Carga el idioma guardado (usado en MainActivity)
    fun loadLocale(context: Context, langCode: String? = null): Context {
        val language = langCode ?: getSavedLanguage(context)
        return updateResources(context, language)
    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    private fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "es") ?: "es"
    }

    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    fun getCurrentLanguage(context: Context): String {
        return getSavedLanguage(context)
    }
}
