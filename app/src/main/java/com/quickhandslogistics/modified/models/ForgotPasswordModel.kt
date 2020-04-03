package com.quickhandslogistics.modified.models

import android.text.TextUtils
import android.util.Log
import com.quickhandslogistics.modified.contracts.ForgotPasswordContract
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener

class ForgotPasswordModel : ForgotPasswordContract.Model {

    override fun resetPasswordUsingEmpId(
        employeeLoginId: String,
        onFinishedListener: ForgotPasswordContract.Model.OnFinishedListener
    ) {
        val forgotPasswordRequest = ForgotPasswordRequest(employeeLoginId)
        DataManager.doPasswordReset(
            forgotPasswordRequest,
            object : ResponseListener<ForgotPasswordResponse> {
                override fun onSuccess(response: ForgotPasswordResponse) {
                    if (response.success) {
                        onFinishedListener.onPasswordResetSuccess(response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(ForgotPasswordModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }

    override fun validatePasswordResetDetails(
        employeeId: String,
        onFinishedListener: ForgotPasswordContract.Model.OnFinishedListener
    ) {
        when {
            TextUtils.isEmpty(employeeId) -> {
                onFinishedListener.emptyEmployeeId()
            }
            else -> {
                onFinishedListener.processPasswordReset(employeeId)
            }
        }
    }

}