package com.quickhandslogistics.modified.models.schedule

import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.utils.DateUtils
import java.util.*
import kotlin.collections.ArrayList

class UnScheduleModel : UnScheduleContract.Model {

    override fun fetchUnScheduleWork(
        onFinishedListener: UnScheduleContract.Model.OnFinishedListener
    ) {
        val unScheduledData = ArrayList<ScheduleData>()

        val cal = Calendar.getInstance()
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )
        cal.add(Calendar.DAY_OF_YEAR, -1)
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )
        cal.add(Calendar.DAY_OF_YEAR, -1)
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )
        unScheduledData.add(
            ScheduleData("", "", "", DateUtils.getDateString(DateUtils.PATTERN_NORMAL, cal.time))
        )

        onFinishedListener.onSuccess(unScheduledData)
    }
}