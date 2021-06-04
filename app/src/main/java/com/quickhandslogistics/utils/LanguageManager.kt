package com.quickhandslogistics.utils

import android.app.Activity
import android.content.res.Configuration
import com.franmontiel.localechanger.LocaleChanger
import java.util.*

object LanguageManager {
    fun setLanguage(activity: Activity, language: String?) {
        LocaleChanger.setLocale(Locale(language))
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        activity.baseContext.resources.updateConfiguration(config, activity.baseContext.resources.displayMetrics)
    }

    enum class Languages(private val language: String) {
        ENG(AppConstant.LANGUAGE_ENGLISH_CODE), SPANISH(AppConstant.LANGUAGE_SPANISH_CODE);

        override fun toString(): String {
            return language
        }
    }
}