package com.quickhandslogistics.modified.data.schedule

data class ScheduleMainResponse(
    val scheduledData: ArrayList<ScheduleData>,
    val unScheduledData: ArrayList<ScheduleData>
)