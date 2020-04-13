package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class AddWorkItemLumpersContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun assignLumpersList(
            workItemId: String,
            workItemType: String,
            selectedLumperIdsList: ArrayList<String>,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse)
            fun onSuccessAssignLumpers()
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun lumperAssignmentFinished()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter {
        fun fetchLumpersList()
        fun onDestroy()
        fun initiateAssigningLumpers(
            selectedLumperIdsList: ArrayList<String>,
            workItemId: String,
            workItemType: String
        )
    }
}