package com.quickhandslogistics.contracts

import com.quickhandslogistics.data.forgotPassword.ForgotPasswordResponse

class ForgotPasswordContract {
    interface Model {
        fun resetPasswordUsingEmpId(employeeLoginId: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun emptyEmployeeId()
            fun onPasswordResetSuccess(forgotPasswordResponse: ForgotPasswordResponse)
            fun processPasswordReset(employeeId: String)
        }

        fun validatePasswordResetDetails(employeeId: String, onFinishedListener: OnFinishedListener)
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showAPISuccessMessage(message: String)
        fun showEmptyEmployeeIdError()
    }

    interface Presenter : BaseContract.Presenter {
        fun validatePasswordResetDetails(employeeId: String)
    }
}