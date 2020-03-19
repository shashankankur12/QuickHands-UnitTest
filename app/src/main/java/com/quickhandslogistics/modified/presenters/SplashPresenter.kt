package com.quickhandslogistics.modified.presenters

import com.quickhandslogistics.modified.contracts.SplashContract
import com.quickhandslogistics.modified.models.SplashModel
import com.quickhandslogistics.utils.SharedPref

class SplashPresenter internal constructor(
    private var splashView: SplashContract.View?,
    sharedPref: SharedPref
) :
    SplashContract.Presenter, SplashContract.Model.OnFinishedListener {

    private val splashModel: SplashModel = SplashModel(sharedPref)

    override fun onDestroy() {
        splashView = null
    }

    override fun decideNextActivity() {
        splashModel.waitForSometime(this)
    }

    override fun onFinished(isLoggedIn: Boolean) {
        splashView?.showNextScreen(isLoggedIn)
    }
}