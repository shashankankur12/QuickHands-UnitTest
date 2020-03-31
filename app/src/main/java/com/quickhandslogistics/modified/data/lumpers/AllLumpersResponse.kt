package com.quickhandslogistics.modified.data.lumpers

data class AllLumpersResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<EmployeeData>
)