package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule

class WorkSheetItemDetailContract {
    interface Model {
        fun fetchWorkItemDetail(workItemId: String, onFinishedListener: OnFinishedListener)
        fun changeWorkItemStatus(workItemId: String, status: String, onFinishedListener: OnFinishedListener)
        fun updateWorkItemNotes(workItemId: String, notesQHLCustomer: String, notesQHL: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: WorkItemDetailAPIResponse)
            fun onSuccessChangeStatus(workItemId: String)
            fun onSuccessUpdateNotes(workItemId: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkItemDetail(workItemDetail: WorkItemDetail, lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?)
        fun statusChangedSuccessfully()
        fun notesSavedSuccessfully()

        interface OnAdapterItemClickListener {
            fun onSelectStatus(status: String)
        }

        interface OnFragmentInteractionListener {
            fun fetchWorkItemDetail(changeResultCode: Boolean)
            fun updateWorkItemNotes(notesQHLCustomer: String, notesQHL: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkItemDetail(workItemId: String)
        fun changeWorkItemStatus(workItemId: String, status: String)
        fun updateWorkItemNotes(workItemId: String, notesQHLCustomer: String, notesQHL: String)
    }
}