package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.data.workSheet.LumpersTimeSchedule

class WorkSheetItemDetailContract {
    interface Model {
        fun fetchWorkItemDetail(workItemId: String, onFinishedListener: OnFinishedListener)
        fun changeWorkItemStatus(
            workItemId: String, status: String, onFinishedListener: OnFinishedListener
        )

        fun updateWorkItemNotes(
            workItemId: String, notesQHLCustomer: String, notesQHL: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(response: WorkItemDetailAPIResponse)
            fun onSuccessChangeStatus(workItemId: String)
            fun onSuccessUpdateNotes(workItemId: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkItemDetail(
            workItemDetail: WorkItemDetail, lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?
        )

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

    interface Presenter {
        fun fetchWorkItemDetail(workItemId: String)
        fun changeWorkItemStatus(workItemId: String, status: String)
        fun updateWorkItemNotes(workItemId: String, notesQHLCustomer: String, notesQHL: String)
        fun onDestroy()
    }
}