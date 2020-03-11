package com.quickhandslogistics.modified.models.schedule

import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.data.schedule.ScheduleMainResponse
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class ScheduleMainModel : ScheduleMainContract.Model {

    override fun fetchSchedules(
        selectedDate: Date,
        onFinishedListener: ScheduleMainContract.Model.OnFinishedListener
    ) {
        val scheduledData = ArrayList<ScheduleData>()
        val unScheduledData = ArrayList<ScheduleData>()

        var size = ThreadLocalRandom.current().nextInt(1, 10)
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
        size = ThreadLocalRandom.current().nextInt(1, 10)
        for (i in 1..size) {
            unScheduledData.add(
                ScheduleData(
                    "Building : One97 Communications Private Limited",
                    "Door : 03",
                    "Work Items : 05",
                    "$i:00 AM"
                )
            )
        }

        val response = ScheduleMainResponse(scheduledData, unScheduledData)
        onFinishedListener.onSuccess(selectedDate, response)
    }
}