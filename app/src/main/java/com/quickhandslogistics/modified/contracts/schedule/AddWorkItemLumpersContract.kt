package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.common.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class AddWorkItemLumpersContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun assignLumpersList(
            workItemId: String, workItemType: String, selectedLumperIdsList: ArrayList<String>, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse)
            fun onSuccessAssignLumpers()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun lumperAssignmentFinished()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
        fun initiateAssigningLumpers(selectedLumperIdsList: ArrayList<String>, workItemId: String, workItemType: String)
    }
}