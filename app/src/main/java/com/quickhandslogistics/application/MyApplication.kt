package com.quickhandslogistics.application

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        mApp = this
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
