package com.quickhandslogistics.application

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        mApp = this

        setLocale()
    }

    private fun setLocale() {
        val selectedLanguage: String =
            SharedPref.getInstance().getString(AppConstant.PREFERENCE_LANGUAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(selectedLanguage)
        } else {
            updateResourcesLegacy(selectedLanguage)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(selectedLanguage: String) {
        val locale = Locale(if (selectedLanguage.isEmpty()) "es" else selectedLanguage)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(selectedLanguage: String) {
        val locale = Locale(if (selectedLanguage.isEmpty()) "es" else selectedLanguage)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    companion object {
        var application: MyApplication? = null
            private set

        var mApp: MyApplication? = null
        fun context(): Context {
            return mApp!!.applicationContext
        }
    }
}
