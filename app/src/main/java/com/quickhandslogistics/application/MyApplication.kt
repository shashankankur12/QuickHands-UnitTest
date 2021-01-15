package com.quickhandslogistics.application

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.franmontiel.localechanger.LocaleChanger
import com.quickhandslogistics.utils.AppConstant
import java.util.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        mApp = this

        val supportedLocales = listOf(Locale(AppConstant.LANGUAGE_ENGLISH_CODE), Locale(AppConstant.LANGUAGE_SPANISH_CODE))
        LocaleChanger.initialize(applicationContext, supportedLocales);
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
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
