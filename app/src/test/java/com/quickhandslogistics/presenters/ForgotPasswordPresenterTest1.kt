package com.quickhandslogistics.presenters

import android.content.res.Resources
import com.google.common.truth.Truth
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.ForgotPasswordContract
import com.quickhandslogistics.data.forgotPassword.ForgotPasswordResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ForgotPasswordPresenterTest1 {
    @Mock
    lateinit var passwordView: ForgotPasswordContract.View

    @Mock
    lateinit var resources: Resources

    @Mock
    lateinit var listner: ForgotPasswordContract.View

    @Mock
    lateinit var listner1: ForgotPasswordContract.Model.OnFinishedListener
    lateinit var forgotPasswordPresenter: ForgotPasswordPresenter

    @Before
    fun setUp() {
        forgotPasswordPresenter = ForgotPasswordPresenter(passwordView, resources)
        successValidation()
    }

    private fun successValidation() {
//        when(forgotPasswordPresenter.validatePasswordResetDetails(any(String.class)))
//                .thenReturn(forgotPasswordPresenter.processPasswordReset(EMPLOYEE_ID));
    }

    @Test
    @Throws(Exception::class)
    fun testValidatePasswordResetDetails_pass_validData() {
//        when(listner.processPasswordReset(EMPLOYEE_ID)).then(EMPLOYEE_ID);
        val ac = ArgumentCaptor.forClass(
            ForgotPasswordResponse::class.java
        )
        forgotPasswordPresenter.validatePasswordResetDetails(EMPLOYEE_ID)
        Mockito.verify(listner1)!!
            .onPasswordResetSuccess(ac.capture())
        ac.allValues
        Truth.assertThat(ac.value).isEqualTo(EMPLOYEE_ID)
    }

    @Test
    @Throws(Exception::class)
    fun testValidatePasswordResetDetails_fail_emptyData() {
        Mockito.`when`(resources.getString(R.string.empty_employee_id_message)).thenReturn(
            EMPTY_MESSAGE
        )
        val ac = ArgumentCaptor.forClass(
            String::class.java
        )
        forgotPasswordPresenter.validatePasswordResetDetails(EMPTY_EMPLOYEE_ID)
        Mockito.verify(listner)!!.showEmptyEmployeeIdError(ac.capture())
        ac.allValues
        Truth.assertThat(ac.value).isEqualTo(EMPTY_MESSAGE)
    }

    @Test
    @Throws(Exception::class)
    fun testValidatePasswordResetDetails_fail_serverError() {
        val ac = ArgumentCaptor.forClass(
            String::class.java
        )
        forgotPasswordPresenter.validatePasswordResetDetails(EMPLOYEE_ID)
        Mockito.verify(listner1)!!
            .onFailure(ac.capture())
        ac.allValues
        Truth.assertThat(ac.value).isEmpty()
    }

    private class ModelImp : ForgotPasswordContract.Model {
        override fun resetPasswordUsingEmpId(
            employeeLoginId: String,
            onFinishedListener: ForgotPasswordContract.Model.OnFinishedListener
        ) {
        }
    }

    companion object {
        const val EMPLOYEE_ID = "employeeId"
        const val EMPTY_EMPLOYEE_ID = ""
        const val EMPTY_MESSAGE = "Please enter Employee Id"
    }
}