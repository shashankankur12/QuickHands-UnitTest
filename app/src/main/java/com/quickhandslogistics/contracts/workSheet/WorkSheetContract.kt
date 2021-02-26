package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse

class WorkSheetContract {
    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchWorkSheetList(onFinishedListener: OnFinishedListener)
        fun saveGroupNoteData(onFinishedListener: OnFinishedListener, cancelled: ArrayList<String>, customerNote: String, qhlNote: String)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchWorkSheet(workSheetListAPIResponse: WorkSheetListAPIResponse)
            fun onSuccessGetHeaderInfo(companyName: String, date: String, shift:String, dept: String)
            fun onSuccessSaveGroupNoteWorkSheet(message: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkSheets(data: WorkSheetListAPIResponse.Data)
        fun showHeaderInfo(companyName: String, date: String, shift: String, dept: String)
        fun successGroupNoteSave(message: String)
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }

        interface OnFragmentInteractionListener {
            fun fetchWorkSheetList()
            fun showBottomSheetGroupNote()
            fun showGroupNote()
            fun removeGroupNote()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkSheetList()
        fun saveGroupNoteData(cancelled: ArrayList<String>, customerNote: String, qhlNote: String)
    }
}