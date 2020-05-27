package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract

class AddLumperTimeWorkSheetItemContract {
    interface Model {
        fun saveLumperTimings(
            id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long, selectedBreakInTime: Long,
            selectedBreakOutTime: Long, waitingTime: String, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun lumpersTimingSaved()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun saveLumperTimings(
            id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long,
            selectedBreakInTime: Long, selectedBreakOutTime: Long, waitingTime: String
        )
    }
}