package com.quickhandslogistics.modified.data.schedule

import java.io.Serializable

data class ScheduleData(
    val title: String,
    val name: String,
    val subService: String,
    val time: String
) : Serializable