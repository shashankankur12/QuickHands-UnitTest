package com.quickhandslogistics.modified.data.schedule

data class AssignLumpersRequest(val buildingId: String, val type: String, val lumperIds: ArrayList<String>)