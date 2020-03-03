package com.quickhandslogistics.model.login

data class Data(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val isActive: Boolean,
    /*val profileImageUrl :Objects,*/
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean,
    val role: String,
    val employeeId: String,
    val token: String
)