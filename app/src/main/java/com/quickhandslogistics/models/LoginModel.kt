package com.quickhandslogistics.models

import android.text.TextUtils
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.quickhandslogistics.contracts.LoginContract
import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.login.LoginRequest
import com.quickhandslogistics.data.login.LoginResponse
import com.quickhandslogistics.data.login.LoginUserData
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_AUTH_TOKEN
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_EMPLOYEE_ID
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel(val sharedPref: SharedPref) : LoginContract.Model {

    override fun fetchEmployeeId(onFinishedListener: LoginContract.Model.OnFinishedListener) {
        val employeeId = sharedPref.getString(PREFERENCE_EMPLOYEE_ID)
        if (employeeId.isNotEmpty()) {
            onFinishedListener.onLoadEmployeeId(employeeId)
        }
    }

    override fun validateLoginDetails(employeeLoginId: String, password: String, onFinishedListener: LoginContract.Model.OnFinishedListener) {
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

    override fun fetchRegistrationToken(employeeLoginId: String, password: String, onFinishedListener: LoginContract.Model.OnFinishedListener) {
        if (sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN).isEmpty()) {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    sharedPref.setString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, token)
                    onFinishedListener.onRegistrationTakenSaved(employeeLoginId, password)
                }
            }
        } else {
            onFinishedListener.onRegistrationTakenSaved(employeeLoginId, password)
        }
    }

    override fun loginUsingEmployeeDetails(employeeLoginId: String, password: String, onFinishedListener: LoginContract.Model.OnFinishedListener) {
        val loginRequest = LoginRequest(employeeLoginId, password, sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN))

        DataManager.getService().doLogin(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onLoginSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(LoginModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun processLoginData(loginUserData: LoginUserData) {
        sharedPref.setString(PREFERENCE_AUTH_TOKEN, loginUserData.token)
        sharedPref.setString(PREFERENCE_EMPLOYEE_ID, loginUserData.employeeId)
    }

    override fun fetchLeadProfileInfo(onFinishedListener: LoginContract.Model.OnFinishedListener) {
        DataManager.getService().getLeadProfile(getAuthToken()).enqueue(object : Callback<LeadProfileAPIResponse> {
            override fun onResponse(call: Call<LeadProfileAPIResponse>, response: Response<LeadProfileAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onLeadProfileSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LeadProfileAPIResponse>, t: Throwable) {
                Log.e(LoginModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: LoginContract.Model.OnFinishedListener) {
        sharedPref.setClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, leadProfileData)
        sharedPref.setString(AppConstant.PREFERENCE_BUILDING_ID, leadProfileData.buildingDetailData?.id)
        onFinishedListener.showNextScreen()
    }
}