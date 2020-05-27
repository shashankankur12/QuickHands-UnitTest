package com.quickhandslogistics.contracts

class SplashContract {
    interface Model {
        fun waitForSometime(delay:Long, onFinishedListener: OnFinishedListener)
        fun performLogout(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onFinished(isLoggedIn: Boolean)
            fun onSuccessLogout()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showNextScreen(isLoggedIn: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun decideNextActivity(delay: Long)
        fun performLogout()
    }
}