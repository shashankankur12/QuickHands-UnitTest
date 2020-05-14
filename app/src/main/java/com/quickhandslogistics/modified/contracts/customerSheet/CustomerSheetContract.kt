package com.quickhandslogistics.modified.contracts.customerSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetScheduleDetails
import java.util.*

class CustomerSheetContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchCustomerSheetList(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchCustomerSheet(customerSheetListAPIResponse: CustomerSheetListAPIResponse, selectedDate: Date)
            fun onSuccessGetHeaderInfo(buildingName: String, date: String)
            fun onSuccessSaveCustomerSheet()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showCustomerSheets(scheduleDetails: CustomerSheetScheduleDetails, customerSheet: CustomerSheetData?, selectedDate: Date)
        fun showHeaderInfo(buildingName: String, date: String)
        fun customerSavedSuccessfully()

        interface OnFragmentInteractionListener {
            fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getCustomerSheetByDate(date: Date)
        fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String)
    }
}