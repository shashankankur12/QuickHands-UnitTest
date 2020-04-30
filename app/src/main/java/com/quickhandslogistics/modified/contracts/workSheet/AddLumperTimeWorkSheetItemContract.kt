package com.quickhandslogistics.modified.contracts.workSheet

class AddLumperTimeWorkSheetItemContract {
    interface Model {
        fun saveLumperTimings(
            id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long,
            selectedBreakInTime: Long, selectedBreakOutTime: Long, waitingTime: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess()
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun lumpersTimingSaved()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter {
        fun onDestroy()
        fun saveLumperTimings(
            id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long,
            selectedBreakInTime: Long, selectedBreakOutTime: Long, waitingTime: String
        )
    }
}