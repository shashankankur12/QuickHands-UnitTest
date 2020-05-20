package com.quickhandslogistics.modified.presenters

import com.quickhandslogistics.modified.contracts.SplashContract
import com.quickhandslogistics.modified.models.SplashModel
import com.quickhandslogistics.utils.SharedPref

class SplashPresenter(private var splashView: SplashContract.View?, sharedPref: SharedPref) :
    SplashContract.Presenter, SplashContract.Model.OnFinishedListener {

    private val splashModel = SplashModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        splashView = null
    }

    override fun decideNextActivity() {
        splashModel.waitForSometime(this)
    }

    /** Model Result Listeners */
    override fun onFinished(isLoggedIn: Boolean) {
        splashView?.showNextScreen(isLoggedIn)
    }
}