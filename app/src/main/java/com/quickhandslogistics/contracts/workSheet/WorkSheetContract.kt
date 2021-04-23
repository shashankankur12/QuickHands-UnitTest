package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.workSheet.ContainerGroupNote
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse

class WorkSheetContract {
    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchWorkSheetList(onFinishedListener: OnFinishedListener)
        fun saveGroupNoteData(onFinishedListener: OnFinishedListener,containerIds: ArrayList<String>, containerType: String, customerNote: String, qhlNote: String)
        fun updateGroupNoteData(onFinishedListener: OnFinishedListener,containerIds: ArrayList<String>, containerType: String, customerNote: String, qhlNote: String, groupNoteId: String)
        fun removeNote(onFinishedListener: OnFinishedListener, id: String)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchWorkSheet(workSheetListAPIResponse: WorkSheetListAPIResponse)
            fun onSuccessGetHeaderInfo(companyName: String, date: String, shift:String, dept: String)
            fun onSuccessSaveGroupNoteWorkSheet(message: String)
            fun onSuccessRemoveNote(message: String)
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
            fun showBottomSheetGroupNote(
                groupNote: ContainerGroupNote?,
                containerIds: ArrayList<String>,
                containerType: String
            )
            fun showGroupNote(noteForCustomer: String, noteForQHL: String)
            fun removeGroupNote(id: String?)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkSheetList()
        fun saveGroupNoteData(containerIds: ArrayList<String>, containerType: String, customerNote: String, qhlNote: String)
        fun updateGroupNoteData(groupNoteId: String, containerIds: ArrayList<String>, containerType: String, customerNote: String, qhlNote: String)
        fun removeNote(id: String)
    }
}