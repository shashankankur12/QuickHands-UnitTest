package com.quickhandslogistics.modified.models

import android.text.TextUtils
import android.util.Log
import com.quickhandslogistics.modified.contracts.LoginContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.login.LoginUserData
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_AUTH_TOKEN
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_EMPLOYEE_ID
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

    override fun processLoginData(loginUserData: LoginUserData) {
        sharedPref.setString(PREFERENCE_AUTH_TOKEN, loginUserData.token)
        sharedPref.setString(PREFERENCE_EMPLOYEE_ID, loginUserData.employeeId)
    }

    override fun fetchLeadProfileInfo(onFinishedListener: LoginContract.Model.OnFinishedListener) {
        DataManager.getLeadProfile(object : ResponseListener<LeadProfileAPIResponse> {
            override fun onSuccess(response: LeadProfileAPIResponse) {
                if (response.success) {
                    onFinishedListener.onLeadProfileSuccess(response)
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

    override fun processLeadProfileData(
        leadProfileData: LeadProfileData,
        onFinishedListener: LoginContract.Model.OnFinishedListener
    ) {
        sharedPref.setClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, leadProfileData)
        sharedPref.setString(
            AppConstant.PREFERENCE_BUILDING_ID,
            leadProfileData.buildingDetailData?.id
        )
        onFinishedListener.showNextScreen()
    }
}