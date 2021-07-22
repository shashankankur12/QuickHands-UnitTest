package com.quickhandslogistics.utils

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ValidationUtilsTest {
    lateinit var validationUtils: ValidationUtils
    @Before
    fun setup() {
        validationUtils = ValidationUtils()
    }

    @Test
    @Throws(Exception::class)
    fun testIsValidUserId_fail_emptyData() {
        val result = validationUtils!!.isValidUserId(EMPTY_EMPLOYEE_ID)
        Truth.assertThat(result).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun testIsValidUserId_pass_emptyData() {
        val result = validationUtils!!.isValidUserId(EMPLOYEE_ID)
        Truth.assertThat(result).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun testLoginValidation_pass_validData() {
        val result = validationUtils!!.loginValidation(EMPLOYEE_ID, PASSWORD)
        Truth.assertThat(result).isEqualTo(ValidationUtils.VALID_PASSWORD)
    }

    @Test
    @Throws(Exception::class)
    fun testLoginValidation_fail_emptyUserId() {
        val result = validationUtils!!.loginValidation(EMPTY_EMPLOYEE_ID, PASSWORD)
        Truth.assertThat(result).isEqualTo(ValidationUtils.EMPTY_USERID)
    }

    @Test
    @Throws(Exception::class)
    fun testLoginValidation_fail_emptyPassword() {
        val result = validationUtils!!.loginValidation(EMPLOYEE_ID, EMPTY_PASSWORD)
        Truth.assertThat(result).isEqualTo(ValidationUtils.EMPTY_PASSWORD)
    }

    @Test
    @Throws(Exception::class)
    fun testLoginValidation_fail_invalidPassword() {
        val result = validationUtils!!.loginValidation(EMPLOYEE_ID, INVALID_PASSWORD)
        Truth.assertThat(result).isEqualTo(ValidationUtils.INVALID_PASSWORD)
    }

    companion object {
        const val EMPLOYEE_ID = "employeeId"
        const val EMPTY_EMPLOYEE_ID = ""
        const val EMPTY_PASSWORD = ""
        const val PASSWORD = "password"
        const val INVALID_PASSWORD = "pass"
    }
}