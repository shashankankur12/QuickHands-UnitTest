package com.quickhandslogistics.contracts.reports

import com.quickhandslogistics.contracts.BaseContract

class CustomerReportContract {
    interface Model {
        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
    }

    interface Presenter : BaseContract.Presenter {
    }
}