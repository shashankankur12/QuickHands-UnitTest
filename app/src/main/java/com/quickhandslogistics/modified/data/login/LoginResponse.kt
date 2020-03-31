package com.quickhandslogistics.modified.data.login

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginUserData
)