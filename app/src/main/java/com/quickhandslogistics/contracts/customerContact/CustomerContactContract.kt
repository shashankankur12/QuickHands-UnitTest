package com.quickhandslogistics.contracts.customerContact

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse

interface CustomerContactContract {

    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchCustomerContactList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: QhlContactListResponse)
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
            fun onEmailViewClick(lumperName: String, email: String)
            fun onChatViewClick(employeeData: EmployeeData)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchCustomerContactList()
    }
}