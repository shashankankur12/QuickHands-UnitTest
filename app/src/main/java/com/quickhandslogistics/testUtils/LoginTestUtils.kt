package com.quickhandslogistics.testUtils

import android.text.TextUtils

object LoginTestUtils {
    fun validateLoginDetails(employeeLoginId: String, password: String): Boolean {
       return when {
            employeeLoginId.isEmpty() -> {
                false
            }
            password.isEmpty() -> {
                false
            }
            password.length < 8 -> {
                false
            }
            else -> {
                true
            }
        }
    }
}