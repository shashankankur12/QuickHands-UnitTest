package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.login.Data
import com.quickhandslogistics.modified.data.login.LoginResponse

class LoginContract {
    interface Model {
        fun fetchEmployeeId(onFinishedListener: OnFinishedListener)
        fun loginUsingEmployeeDetails(
            employeeLoginId: String,
            password: String,
            onFinishedListener: OnFinishedListener
        )

        fun processLoginData(data: Data, onFinishedListener: OnFinishedListener)
        fun validateLoginDetails(
            employeeLoginId: String,
            password: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun emptyEmployeeId()
            fun emptyPassword()
            fun invalidPassword()
            fun onFailure(message: String = "")
            fun onLoadEmployeeId(employeeId: String)
            fun onLoginSuccess(loginResponse: LoginResponse)
            fun processCredentials(employeeLoginId: String, password: String)
            fun showNextScreen()
        }
    }

    interface View {
        fun hideProgress()
        fun loadEmployeeId(employeeId: String)
        fun showAPIErrorMessage(message: String)
        fun showEmptyEmployeeIdError()
        fun showEmptyPasswordError()
        fun showInvalidPasswordError()
        fun showNextScreen()
        fun showProgress(message: String)
    }

    interface Presenter {
        fun loadEmployeeId()
        fun onDestroy()
        fun validateLoginDetails(employeeLoginId: String, password: String)
    }
}