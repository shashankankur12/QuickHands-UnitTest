package com.quickhandslogistics.contracts.customerContact

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse

interface CustomerContactContract {

    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchCustomerContactList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperListAPIResponse)
            fun onSuccessGetHeaderInfo(leadProfileData: BuildingDetailData?)
        }
    }

    interface View : BaseContract.View {
        fun showHeaderInfo(leadProfileData: BuildingDetailData?)
        fun showAPIErrorMessage(message: String)
        fun showCustomerContactData(employeeDataList: ArrayList<EmployeeData>)
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
            fun onPhoneViewClick(lumperName: String, phone: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchCustomerContactList()
    }
}