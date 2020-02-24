package com.quickhandslogistics.model.login

import com.quickhandslogistics.model.error.Error

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: List<Data>
)