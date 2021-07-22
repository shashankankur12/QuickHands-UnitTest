package com.quickhandslogistics.contracts

import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.login.LoginResponse
import com.quickhandslogistics.data.login.LoginUserData

class LoginContract {
    interface Model {
        fun fetchEmployeeId(onFinishedListener: OnFinishedListener)
        fun loginUsingEmployeeDetails(employeeLoginId: String, password: String, onFinishedListener: OnFinishedListener)
        fun processLoginData(loginUserData: LoginUserData)
        fun fetchRegistrationToken(employeeLoginId: String, password: String, onFinishedListener: OnFinishedListener)
        fun fetchLeadProfileInfo(onFinishedListener: OnFinishedListener)
        fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onLeadProfileSuccess(leadProfileAPIResponse: LeadProfileAPIResponse)
            fun onLoadEmployeeId(employeeId: String)
            fun onLoginSuccess(loginResponse: LoginResponse)
            fun onRegistrationTakenSaved(employeeLoginId: String, password: String)
            fun showNextScreen()
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