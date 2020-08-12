package com.quickhandslogistics.contracts.scheduleTime

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchSchedulesTimeByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun cancelScheduleLumpers(lumperId: String, date: Date, onFinishedListener:OnFinishedListener)
        fun editScheduleLumpers(lumperId: String, date: Date ,timeMilsec :Long, onFinishedListener:OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(selectedDate: Date, scheduleTimeAPIResponse: GetScheduleTimeAPIResponse)
            fun onSuccessGetHeaderInfo(dateString: String)
            fun onSuccessRequest(date: Date)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showAPIErrorMessage(message: String)
        fun showNotesData(notes: String?)
        fun showSuccessDialog(message:String, date: Date)
        fun showScheduleTimeData(
            selectedDate: Date,
            scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>,
            tempLumperIds: ArrayList<String>,
            notes: String?
        )
        fun showLoginScreen()
        interface OnAdapterItemClickListener {
            fun onEditTimeClick(
                adapterPosition: Int,
                timeInMillis: Long,
                details: ScheduleTimeDetail
            )
            fun onScheduleNoteClick(adapterPosition: Int, notes: String)
            fun onAddRemoveClick(
                adapterPosition: Int,
                details: ScheduleTimeDetail
            )
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getSchedulesTimeByDate(date: Date)
        fun cancelScheduleLumpers(lumperId: String, date: Date)
        fun editScheduleLumpers(lumperId: String, date: Date ,timeMilsec :Long)
    }
}