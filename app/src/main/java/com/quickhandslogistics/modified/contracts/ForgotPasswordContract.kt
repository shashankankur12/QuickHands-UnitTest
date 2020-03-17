package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse

class ForgotPasswordContract {
    interface Model {

        fun resetPasswordUsingEmpId(
            employeeLoginId: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun emptyEmployeeId()
            fun onFailure(message: String = "")
            fun onPasswordResetSuccess(forgotPasswordResponse: ForgotPasswordResponse)
            fun processPasswordReset(employeeId: String)
        }

        fun validatePasswordResetDetails(
            employeeLoginId: String,
            onFinishedListener: OnFinishedListener
        )
    }

    interface View {
        fun hideProgress()
        fun showAPIErrorMessage(message: String)
        fun showAPISuccessMessage(message: String)
        fun showEmptyEmployeeIdError()
        fun showProgress(message: String)
    }

    interface Presenter {
        fun onDestroy()
        fun validatePasswordResetDetails(employeeId: String)
    }
}