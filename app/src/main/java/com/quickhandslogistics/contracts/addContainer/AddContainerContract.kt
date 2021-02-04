package com.quickhandslogistics.contracts.addContainer

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.lumpers.EmployeeData

interface AddContainerContract {
    interface Model {
        fun addTodayWorkContainer(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessAddTodayWorkContainer()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun cancellingWorkScheduleFinished()
        fun showLoginScreen()
        interface OnAdapterItemClickListener {
            fun onLumperSelectionChanged()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun addTodayWorkContainer(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String)
    }
}