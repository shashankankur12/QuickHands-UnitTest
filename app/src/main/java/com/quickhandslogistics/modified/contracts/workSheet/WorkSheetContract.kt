package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse

class WorkSheetContract {
    interface Model {
        fun fetchHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchWorkSheetList(onFinishedListener: OnFinishedListener)
        fun cancelAllWorkSchedules(
            selectedLumperIdsList: ArrayList<String>,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessFetchWorkSheet(workSheetListAPIResponse: WorkSheetListAPIResponse)
            fun onSuccessCancelWorkSchedules()
            fun onSuccessGetHeaderInfo(buildingName: String, date: String)
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showWorkSheets(
            onGoingWorkItems: ArrayList<WorkItemDetail>,
            cancelledWorkItems: List<WorkItemDetail>,
            completedWorkItems: List<WorkItemDetail>
        )
        fun cancellingWorkScheduleFinished()
        fun showHeaderInfo(buildingName: String, date: String)

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter {
        fun fetchWorkSheetList()
        fun onDestroy()
        fun initiateCancellingWorkSchedules(selectedLumperIdsList: ArrayList<String>)
    }
}