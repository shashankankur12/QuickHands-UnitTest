package com.quickhandslogistics.contracts

import com.quickhandslogistics.data.ErrorResponse

open class BaseContract {
    interface Model {
        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onErrorCode(errorCode: ErrorResponse)
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