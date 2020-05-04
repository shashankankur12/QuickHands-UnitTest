package com.quickhandslogistics.modified.contracts.customerSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import java.util.*

class CustomerSheetContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchCustomerSheetList(selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessFetchCustomerSheet(workSheetListAPIResponse: WorkSheetListAPIResponse)
            fun onSuccessGetHeaderInfo(buildingName: String, date: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showCustomerSheets(data: WorkSheetListAPIResponse.Data)
        fun showHeaderInfo(buildingName: String, date: String)

        interface OnAdapterItemClickListener {
        }

        interface OnFragmentInteractionListener {
        }
    }

    interface Presenter {
        fun getCustomerSheetByDate(date: Date)
        fun onDestroy()
    }
}