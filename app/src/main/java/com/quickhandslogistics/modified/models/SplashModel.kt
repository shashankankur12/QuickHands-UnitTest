package com.quickhandslogistics.modified.models

import android.os.Handler
import com.quickhandslogistics.modified.contracts.SplashContract

class SplashModel : SplashContract.Model {

    companion object {
        private const val DELAY: Long = 2000
    }

    override fun waitForSometime(onFinishedListener: SplashContract.Model.OnFinishedListener?) {
        Handler().postDelayed({
            onFinishedListener?.onFinished()
        }, DELAY)
    }
}