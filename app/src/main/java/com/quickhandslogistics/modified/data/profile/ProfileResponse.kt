package com.quickhandslogistics.modified.data.profile

data class ProfileResponse (
    val success: Boolean,
    val message: String,
    val data: LeadProfileData
)