package com.quickhandslogistics.modified.presenters

import com.quickhandslogistics.modified.contracts.SplashContract
import com.quickhandslogistics.modified.models.SplashModel

class SplashPresenter internal constructor(private var splashView: SplashContract.View?) :
    SplashContract.Presenter, SplashContract.Model.OnFinishedListener {

    private val splashModel: SplashModel = SplashModel()

    override fun onDestroy() {
        splashView = null
    }

    override fun decideNextActivity() {
        splashModel.waitForSometime(this)
    }

    override fun onFinished() {
        splashView?.showNextScreen()
    }
}