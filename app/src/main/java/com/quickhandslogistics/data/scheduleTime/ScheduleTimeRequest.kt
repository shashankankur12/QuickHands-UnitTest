package com.quickhandslogistics.data.scheduleTime

data class ScheduleTimeRequest(val lumpers: ArrayList<LumperScheduleTimeData>, val notesForLead: String, val day: String)

data class LumperScheduleTimeData(val startTime: Long, val lumperId: String)