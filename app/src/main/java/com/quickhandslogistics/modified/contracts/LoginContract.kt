package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.login.LoginUserData

class LoginContract {
    interface Model {
        fun fetchEmployeeId(onFinishedListener: OnFinishedListener)
        fun loginUsingEmployeeDetails(employeeLoginId: String, password: String, onFinishedListener: OnFinishedListener)

        fun processLoginData(loginUserData: LoginUserData)
        fun validateLoginDetails(employeeLoginId: String, password: String, onFinishedListener: OnFinishedListener)

        fun fetchRegistrationToken(employeeLoginId: String, password: String, onFinishedListener: OnFinishedListener)

        fun fetchLeadProfileInfo(onFinishedListener: OnFinishedListener)
        fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun emptyEmployeeId()
            fun emptyPassword()
            fun invalidPassword()
            fun onLoadEmployeeId(employeeId: String)
            fun onLoginSuccess(loginResponse: LoginResponse)
            fun processCredentials(employeeLoginId: String, password: String)
            fun onRegistrationTakenSaved(employeeLoginId: String, password: String)
            fun showNextScreen()
            fun onLeadProfileSuccess(leadProfileAPIResponse: LeadProfileAPIResponse)
        }
    }

    interface View : BaseContract.View {
        fun loadEmployeeId(employeeId: String)
        fun showAPIErrorMessage(message: String)
        fun showEmptyEmployeeIdError()
        fun showEmptyPasswordError()
        fun showInvalidPasswordError()
        fun showNextScreen()
    }

    interface Presenter : BaseContract.Presenter {
        fun loadEmployeeId()
        fun validateLoginDetails(employeeLoginId: String, password: String)
    }
}