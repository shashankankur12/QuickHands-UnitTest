package com.quickhandslogistics.modified.contracts

open class BaseContract {
    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
    }
}