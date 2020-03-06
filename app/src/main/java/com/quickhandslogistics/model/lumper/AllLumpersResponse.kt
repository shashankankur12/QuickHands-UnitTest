package com.quickhandslogistics.model.lumper

data class AllLumpersResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<LumperData>
)