package com.quickhandslogistics.data.schedule

data class AssignLumpersRequest(val buildingId: String, val type: String, val lumperIds: ArrayList<String>, val tempLumperIds: ArrayList<String>)