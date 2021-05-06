package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import okhttp3.MultipartBody
import java.util.*
import kotlin.collections.ArrayList

class WorkSheetItemDetailContract {
    interface Model {
        fun fetchWorkItemDetail(workItemId: String, onFinishedListener: OnFinishedListener)
        fun changeWorkItemStatus(
            workItemId: String,
            status: String,
            selectedDate: Date? =null,
            selectedTime: Long? =null,
            onFinishedListener: OnFinishedListener
        )

        fun updateWorkItemNotes(
            workItemId: String,
            notesQHLCustomer: String,
            notesQHL: String,
            noteImageArrayList: ArrayList<String>,
            onFinishedListener: OnFinishedListener
        )

        fun removeLumper(lumperIds: ArrayList<String>, tempLumperIds: ArrayList<String>, workItemId: String, onFinishedListener: OnFinishedListener)
        fun uploadeNoteImage(fileName: MultipartBody.Part, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: WorkItemDetailAPIResponse)
            fun onSuccessChangeStatus(workItemId: String)
            fun onSuccessUpdateNotes(workItemId: String)
            fun onSuccessUploadImage(imageUrl: UploadImageResponse?)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkItemDetail(
            container: WorkItemContainerDetails,
            lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?,
            buildingParams: ArrayList<String>?
        )

        fun statusChangedSuccessfully()
        fun notesSavedSuccessfully()
        fun showLoginScreen()
        fun onSuccessUploadImage(imageUrl: String)

        interface OnAdapterItemClickListener {
            fun onSelectStatus(status: String)
        }

        interface OnFragmentInteractionListener {
            fun fetchWorkItemDetail(changeResultCode: Boolean)
            fun updateWorkItemNotes(
                notesQHLCustomer: String,
                notesQHL: String,
                noteImageArrayList: ArrayList<String>
            )
            fun dataChanged(isChanged: Boolean)
            fun removeLumperFromSchedule(lumperIds: ArrayList<String>, tempLumperIds: ArrayList<String>)
            fun uploadNoteImage(imageFileName: MultipartBody.Part)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkItemDetail(workItemId: String)
        fun changeWorkItemStatus(
            workItemId: String,
            status: String,
            selectedDate: Date? =null,
            selectedTime: Long? =null
        )
        fun updateWorkItemNotes(
            workItemId: String,
            notesQHLCustomer: String,
            notesQHL: String,
            noteImageArrayList: ArrayList<String>
        )
        fun removeLumper(lumperIds: ArrayList<String>, tempLumperIds: ArrayList<String>, workItemId: String)
        fun uploadNoteImage(imageFileName: MultipartBody.Part)
    }
}