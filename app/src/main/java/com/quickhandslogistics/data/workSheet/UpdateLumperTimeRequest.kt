package com.quickhandslogistics.data.workSheet

data class UpdateLumperTimeRequest(val lumperId: String, val workItemId: String, val timingDetails: TimingDetails)

class TimingDetails {
    var startTime: Long? = null
    var endTime: Long? = null
    var breakTimeStart: Long? = null
    var breakTimeEnd: Long? = null
    var waitingTime: Int? = null
    var partWork : Int? = null
}