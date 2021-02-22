package com.quickhandslogistics.contracts.qhlContact

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse

interface QhlContactContract {

    interface Model {
        fun fetchQhlHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchQhlContactList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperListAPIResponse)
            fun onSuccessGetHeaderInfo(leadProfileData: LeadProfileData?)
        }
    }

    interface View : BaseContract.View {
        fun showQhlHeaderInfo(leadProfileData: LeadProfileData?)
        fun showAPIErrorMessage(message: String)
        fun qhlContactList(employeeDataList: ArrayList<EmployeeData>)
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
            fun onPhoneViewClick(lumperName: String, phone: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchQhlContactList()
    }
}