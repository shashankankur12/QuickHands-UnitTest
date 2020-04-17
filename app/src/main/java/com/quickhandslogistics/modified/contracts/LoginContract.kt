package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.data.login.LoginUserData
import com.quickhandslogistics.modified.data.login.LoginResponse

class LoginContract {
    interface Model {
        fun fetchEmployeeId(onFinishedListener: OnFinishedListener)
        fun loginUsingEmployeeDetails(
            employeeLoginId: String,
            password: String,
            onFinishedListener: OnFinishedListener
        )

        fun processLoginData(loginUserData: LoginUserData)
        fun validateLoginDetails(
            employeeLoginId: String,
            password: String,
            onFinishedListener: OnFinishedListener
        )

        fun fetchRegistrationToken(employeeLoginId: String, password: String, onFinishedListener: OnFinishedListener)
        fun fetchLeadProfileInfo(onFinishedListener: OnFinishedListener)
        fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun emptyEmployeeId()
            fun emptyPassword()
            fun invalidPassword()
            fun onFailure(message: String = "")
            fun onLoadEmployeeId(employeeId: String)
            fun onLoginSuccess(loginResponse: LoginResponse)
            fun processCredentials(employeeLoginId: String, password: String)
            fun onRegistrationTakenSaved(employeeLoginId: String, password: String)
            fun showNextScreen()
            fun onLeadProfileSuccess(leadProfileAPIResponse: LeadProfileAPIResponse)
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