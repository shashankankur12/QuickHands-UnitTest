package com.quickhandslogistics.modified.models

import android.os.Handler
import com.quickhandslogistics.modified.contracts.SplashContract
import com.quickhandslogistics.modified.data.login.UserData
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.ValueUtils

class SplashModel(private val sharedPref: SharedPref) : SplashContract.Model {

    companion object {
        private const val DELAY: Long = 2000
    }

    override fun waitForSometime(onFinishedListener: SplashContract.Model.OnFinishedListener?) {
        Handler().postDelayed({
            val leadProfile = sharedPref.getClassObject(
                PREFERENCE_LEAD_PROFILE,
                UserData::class.java
            ) as UserData?
            leadProfile?.also {
                onFinishedListener?.onFinished(isLoggedIn = ValueUtils.getDefaultOrValue(leadProfile.isActive))
            } ?: run {
                onFinishedListener?.onFinished(isLoggedIn = false)
            }
        }, DELAY)
    }
}