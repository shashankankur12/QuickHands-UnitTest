package com.quickhandslogistics.utils

import android.content.res.Resources
import com.quickhandslogistics.application.MyApplication

class ResourceManager {

    companion object {
        private var instance: Resources? = null

        fun getInstance(): Resources {
            if (instance == null)
                instance = MyApplication.mApp!!.resources

            return instance!!
        }
    }
}