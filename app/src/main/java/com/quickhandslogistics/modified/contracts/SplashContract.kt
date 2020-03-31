package com.quickhandslogistics.modified.contracts

class SplashContract {
    interface Model {
        fun waitForSometime(onFinishedListener: OnFinishedListener?)
        interface OnFinishedListener {
            fun onFinished(isLoggedIn: Boolean)
        }
    }

    interface View {
        fun showNextScreen(isLoggedIn: Boolean)
    }

    interface Presenter {
        fun onDestroy()
        fun decideNextActivity()
    }
}