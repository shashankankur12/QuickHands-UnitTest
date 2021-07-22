package com.quickhandslogistics.contracts

import com.quickhandslogistics.data.forgotPassword.ForgotPasswordResponse

class ForgotPasswordContract {
    interface Model {
        fun resetPasswordUsingEmpId(employeeLoginId: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onPasswordResetSuccess(forgotPasswordResponse: ForgotPasswordResponse)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showAPISuccessMessage(message: String)
        fun showEmptyEmployeeIdError(string: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun validatePasswordResetDetails(employeeId: String)
    }
}