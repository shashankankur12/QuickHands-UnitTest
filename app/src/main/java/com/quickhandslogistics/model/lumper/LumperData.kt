package com.quickhandslogistics.model.lumper

import java.io.Serializable

data class LumperData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: String,
   // val addedBy: String,
   // val lumpers: String,
   // val profileImageUrl: String,
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean,
    val isActive: Boolean,
    val password: String,
    val created_at: String,
    val updated_at: String
) : Serializable