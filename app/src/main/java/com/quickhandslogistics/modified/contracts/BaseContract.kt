package com.quickhandslogistics.modified.contracts

open class BaseContract {

    interface Model {
        interface OnFinishedListener {
            fun onFailure(message: String = "")
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
    }

    interface Presenter {
        fun onDestroy()
    }
}