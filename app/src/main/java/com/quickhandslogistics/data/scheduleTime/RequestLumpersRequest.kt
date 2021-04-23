package com.quickhandslogistics.data.scheduleTime

data class RequestLumpersRequest(val requestedLumpersCount: Int, val notesForDM: String, val day: String, val notesForLumper: String, val startTime: String)