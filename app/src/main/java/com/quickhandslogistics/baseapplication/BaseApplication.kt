package com.quickhandslogistics.baseapplication

import android.app.Application
import android.content.res.Configuration
import co.clicke.databases.SharedPreferenceHandler
import java.util.*


class BaseApplication : Application() {
    val languages:String ?= null

    override fun onCreate() {
        super.onCreate()
        SharedPreferenceHandler.getInstance(this)
            val locale = Locale("es")
            Locale.setDefault(locale)

            val config = Configuration()
            config.locale = locale

            resources.updateConfiguration(
                config,
                resources.displayMetrics
            )
        }
    }