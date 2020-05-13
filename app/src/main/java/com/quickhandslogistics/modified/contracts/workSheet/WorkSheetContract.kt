package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse

class WorkSheetContract {
    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchWorkSheetList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchWorkSheet(workSheetListAPIResponse: WorkSheetListAPIResponse)
            fun onSuccessGetHeaderInfo(buildingName: String, date: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkSheets(data: WorkSheetListAPIResponse.Data)
        fun showHeaderInfo(buildingName: String, date: String)

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }

        interface OnFragmentInteractionListener {
            fun fetchWorkSheetList()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkSheetList()
    }
}