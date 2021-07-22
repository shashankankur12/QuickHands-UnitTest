package com.quickhandslogistics.utils

class ValidationUtils {

    companion object {
        private var validationUtils: ValidationUtils? = null
        const val EMPTY_PASSWORD = "EMPTY_PASSWORD"
        const val EMPTY_USERID = "EMPTY_USERID"
        const val INVALID_PASSWORD = "INVALID_PASSWORD"
        const val VALID_PASSWORD = "VALID_PASSWORD"

        fun getInstance(): ValidationUtils {
            if (validationUtils == null) {
                validationUtils = ValidationUtils()
            }
            return validationUtils as ValidationUtils
        }
    }
    fun isValidUserId(employeeId: String): Boolean {
        return  !employeeId.isNullOrEmpty()
    }


    fun loginValidation(employeeLoginId: String, password: String): String {
        return when {
            (employeeLoginId.isNullOrEmpty()) -> {
                EMPTY_USERID
            }
            (password.isNullOrEmpty()) -> {
                EMPTY_PASSWORD
            }
            (!password.isNullOrEmpty() && password.length < 8) -> {
                INVALID_PASSWORD
            }
            else -> {
                VALID_PASSWORD
            }
        }
    }
}