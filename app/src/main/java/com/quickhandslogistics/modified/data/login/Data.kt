package com.quickhandslogistics.modified.data.login

data class Data(
    val email: String,
    val employeeId: String,
    val firstName: String,
    val id: String,
    val isActive: Boolean,
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean,
    val lastName: String,
    val phone: String,
//    val profileImageUrl :Objects,
    val role: String,
    val token: String
)