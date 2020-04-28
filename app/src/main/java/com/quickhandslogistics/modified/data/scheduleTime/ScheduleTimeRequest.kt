package com.quickhandslogistics.modified.data.scheduleTime

data class ScheduleTimeRequest(
    val lumpers: ArrayList<LumperScheduleTimeData>,
    val notesForLead: String,
    val requestedLumpersCount: Int,
    val notesForDM: String,
    val day: String
)

data class LumperScheduleTimeData(val startTime: Long, val lumperId: String)