package com.quickhandslogistics.modified.contracts

class SplashContract {
    interface Model {
        fun waitForSometime(onFinishedListener: OnFinishedListener?)
        interface OnFinishedListener {
            fun onFinished()
        }
    }

    interface View {
        fun showNextScreen()
    }

    interface Presenter {
        fun onDestroy()
        fun decideNextActivity()
    }
}