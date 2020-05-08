package com.quickhandslogistics.modified.models

import android.text.TextUtils
import android.util.Log
import com.quickhandslogistics.modified.contracts.ForgotPasswordContract
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordModel : ForgotPasswordContract.Model {

    override fun resetPasswordUsingEmpId(employeeLoginId: String, onFinishedListener: ForgotPasswordContract.Model.OnFinishedListener) {
        val forgotPasswordRequest = ForgotPasswordRequest(employeeLoginId)

        DataManager.getService().doResetPassword(forgotPasswordRequest).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onPasswordResetSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Log.e(ForgotPasswordModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun validatePasswordResetDetails(employeeId: String, onFinishedListener: ForgotPasswordContract.Model.OnFinishedListener) {
        if (TextUtils.isEmpty(employeeId)) {
            onFinishedListener.emptyEmployeeId()
        } else {
            onFinishedListener.processPasswordReset(employeeId)
        }
    }

}