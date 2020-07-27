package com.quickhandslogistics.contracts.lumpers

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import java.util.*
import kotlin.collections.ArrayList

class LumpersContract {
    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperListAPIResponse)
            fun onSuccessGetHeaderInfo(dateString: String)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
            fun onPhoneViewClick(lumperName: String, phone: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
    }
}