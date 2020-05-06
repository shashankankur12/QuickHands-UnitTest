package com.quickhandslogistics.modified.contracts.customerSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import java.util.*

class CustomerSheetContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchCustomerSheetList(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun saveCustomerSheet(
            customerName: String, notesCustomer: String, signatureFilePath: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessFetchCustomerSheet(
                customerSheetListAPIResponse: CustomerSheetListAPIResponse, selectedDate: Date
            )

            fun onSuccessGetHeaderInfo(buildingName: String, date: String)
            fun onSuccessSaveCustomerSheet()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showCustomerSheets(
            scheduleDetails: CustomerSheetListAPIResponse.ScheduleDetails,
            customerSheet: CustomerSheetListAPIResponse.CustomerSheetData?,
            selectedDate: Date
        )

        fun showHeaderInfo(buildingName: String, date: String)
        fun customerSavedSuccessfully()

        interface OnFragmentInteractionListener {
            fun saveCustomerSheet(
                customerName: String, notesCustomer: String, signatureFilePath: String
            )
        }
    }

    interface Presenter {
        fun getCustomerSheetByDate(date: Date)
        fun saveCustomerSheet(
            customerName: String, notesCustomer: String, signatureFilePath: String
        )
        fun onDestroy()
    }
}