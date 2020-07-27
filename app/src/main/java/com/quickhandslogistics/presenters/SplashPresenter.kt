package com.quickhandslogistics.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.SplashContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.models.SplashModel
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.views.SplashActivity.Companion.SPLASH_AFTER_LOGOUT_DELAY

class SplashPresenter(private var splashView: SplashContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    SplashContract.Presenter, SplashContract.Model.OnFinishedListener {

    private val splashModel = SplashModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        splashView = null
    }

    override fun decideNextActivity(delay: Long) {
        splashModel.waitForSometime(delay, this)
    }

    override fun performLogout() {
        splashView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        splashModel.performLogout(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        splashView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            splashView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            splashView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
       onFailure(errorCode.message)
    }

    override fun onFinished(isLoggedIn: Boolean) {
        splashView?.showNextScreen(isLoggedIn)
    }

    override fun onSuccessLogout() {
        splashView?.hideProgressDialog()
        decideNextActivity(SPLASH_AFTER_LOGOUT_DELAY)
    }
}