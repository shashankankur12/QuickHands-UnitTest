package com.quickhandslogistics.modified.models.schedule

import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class ScheduleModel : ScheduleContract.Model {

    override fun fetchSchedules(
        selectedDate: Date,
        onFinishedListener: ScheduleContract.Model.OnFinishedListener
    ) {
        val scheduledData = ArrayList<ScheduleData>()

        val size = ThreadLocalRandom.current().nextInt(1, 10)
        for (i in 1..size) {
            scheduledData.add(
                ScheduleData(
                    "Building : One97 Communications Private Limited",
                    "Door : 03",
                    "Work Items : 05",
                    "$i:00 AM"
                )
            )
        }

        onFinishedListener.onSuccess(selectedDate, scheduledData)
    }
}