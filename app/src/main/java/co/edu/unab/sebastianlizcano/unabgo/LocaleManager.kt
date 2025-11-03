package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

object LocaleManager {
    private const val PREF_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "language"

    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)
        return updateResources(context, languageCode)
    }

    fun loadLocale(context: Context): Context {
        val lang = getSavedLanguage(context)
        return updateResources(context, lang)
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
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    fun getCurrentLanguage(context: Context): String {
        return getSavedLanguage(context)
    }
}
