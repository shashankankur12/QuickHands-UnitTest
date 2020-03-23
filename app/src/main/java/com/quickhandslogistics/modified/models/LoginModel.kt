package com.quickhandslogistics.modified.models

import android.text.TextUtils
import android.util.Log
import com.quickhandslogistics.modified.contracts.LoginContract
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.login.UserData
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_AUTH_TOKEN
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_EMPLOYEE_ID
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.StringUtils

class LoginModel(val sharedPref: SharedPref) : LoginContract.Model {

    override fun fetchEmployeeId(onFinishedListener: LoginContract.Model.OnFinishedListener) {
        val employeeId = sharedPref.getString(PREFERENCE_EMPLOYEE_ID)
        if (!StringUtils.isNullOrEmpty(employeeId)) {
            onFinishedListener.onLoadEmployeeId(employeeId)
        }
    }

    override fun validateLoginDetails(
        employeeLoginId: String,
        password: String,
        onFinishedListener: LoginContract.Model.OnFinishedListener
    ) {
        when {
            TextUtils.isEmpty(employeeLoginId) -> {
                onFinishedListener.emptyEmployeeId()
            }
            TextUtils.isEmpty(password) -> {
                onFinishedListener.emptyPassword()
            }
            password.length < 7 -> {
                onFinishedListener.invalidPassword()
            }
            else -> {
                onFinishedListener.processCredentials(employeeLoginId, password)
            }
        }
    }

    override fun loginUsingEmployeeDetails(
        employeeLoginId: String,
        password: String,
        onFinishedListener: LoginContract.Model.OnFinishedListener
    ) {
        val loginRequest = LoginRequest(employeeLoginId, password)
        DataManager.doLogin(loginRequest, object : ResponseListener<LoginResponse> {
            override fun onSuccess(response: LoginResponse) {
                if (response.success) {
                    onFinishedListener.onLoginSuccess(response)
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(LoginModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun processLoginData(
        userData: UserData,
        onFinishedListener: LoginContract.Model.OnFinishedListener
    ) {
        sharedPref.setClassObject(PREFERENCE_LEAD_PROFILE, userData)
        sharedPref.setString(PREFERENCE_AUTH_TOKEN, userData.token)
        sharedPref.setString(PREFERENCE_EMPLOYEE_ID, userData.employeeId)
        onFinishedListener.showNextScreen()
    }
}