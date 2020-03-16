package com.quickhandslogistics.modified.models

import android.text.TextUtils
import android.util.Log
import com.quickhandslogistics.modified.data.login.Data
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.contracts.LoginContract
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_EMPLOYEE_ID
import com.quickhandslogistics.utils.SharedPref

class LoginModel(val sharedPref: SharedPref) : LoginContract.Model {

    override fun fetchEmployeeId(
        onFinishedListener: LoginContract.Model.OnFinishedListener
    ) {
        if (!TextUtils.isEmpty(sharedPref.getString(PREFERENCE_EMPLOYEE_ID))) {
            onFinishedListener.onLoadEmployeeId(sharedPref.getString(PREFERENCE_EMPLOYEE_ID))
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
                }
                onFinishedListener.onFailure()
            }
        })
    }

    override fun processLoginData(
        data: Data,
        onFinishedListener: LoginContract.Model.OnFinishedListener
    ) {
        SharedPref.getInstance().setBoolean(AppConstant.PREFERENCE_IS_ACTIVE, data.isActive)
        SharedPref.getInstance().setString(AppConstant.PREFERENCE_AUTH_TOKEN, data.token)
        SharedPref.getInstance().setString(PREFERENCE_EMPLOYEE_ID, data.employeeId)
        SharedPref.getInstance().setString(AppConstant.PREFERENCE_USER_PHONE, data.phone)
        SharedPref.getInstance().setString(AppConstant.PREFERENCE_USER_EMAIL, data.email)
        SharedPref.getInstance().setString(AppConstant.PREFERENCE_USER_ROLE, data.role)
        SharedPref.getInstance()
            .setString(AppConstant.PREFERENCE_USER_FULL_NAME, "${data.firstName} ${data.lastName}")
        SharedPref.getInstance().setString(AppConstant.PREFERENCE_USER_FIRST_NAME, data.firstName)
        SharedPref.getInstance().setString(AppConstant.PREFERENCE_USER_LAST_NAME, data.lastName)

        onFinishedListener.showNextScreen()
    }
}