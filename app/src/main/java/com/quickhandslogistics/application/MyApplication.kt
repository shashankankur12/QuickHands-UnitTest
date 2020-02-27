package com.quickhandslogistics.application

import android.app.Application
import android.content.Context
import co.clicke.databases.SharedPreferenceHandler
import io.bloco.faker.components.App

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        mApp = this
        SharedPreferenceHandler.getInstance(this)

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
