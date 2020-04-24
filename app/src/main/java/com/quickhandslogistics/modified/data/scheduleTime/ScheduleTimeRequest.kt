package com.quickhandslogistics.modified.data.scheduleTime

data class ScheduleTimeRequest(
    val lumpers: ArrayList<LumperScheduleTimeData>,
    val notes: String,
    val requestlumpersCount: Int,
    val notesForDM: String
)

data class LumperScheduleTimeData(val startTime: Long, val lumperId: String)