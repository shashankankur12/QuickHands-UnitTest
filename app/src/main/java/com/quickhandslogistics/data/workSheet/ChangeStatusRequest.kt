package com.quickhandslogistics.data.workSheet

data class ChangeStatusRequest(
    val status: String,
    val startTime: Long? = null,
    val dayToBeResumed: String? = null
)