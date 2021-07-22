package com.quickhandslogistics.presenters

import android.content.res.Resources
import com.google.common.truth.Truth
import com.quickhandslogistics.contracts.LoginContract
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginPresenterTest {

    @Mock
    lateinit var passwordView: LoginContract.View

    @Mock
    lateinit var resources: Resources

    @Mock
    lateinit var sharedPref: SharedPref

    @Mock
    lateinit var listner1: LoginContract.Model.OnFinishedListener
    lateinit var loginPresenter: LoginPresenter

    val EMPLOYEE_ID = "employeeId"
    val EMPTY_EMPLOYEE_ID = ""
    val EMPTY_PASSWORD = ""
    val PASSWORD = "password"
    val INVALID_PASSWORD = "pass"
    val EMPTY_MESSAGE = "Please enter Employee Id"
    @Before
    fun setUp() {

        loginPresenter = LoginPresenter(passwordView, resources, sharedPref)
    }

    @Test
    fun validateLoginDetails_fail_emptyID(){
        val ac = ArgumentCaptor.forClass(
            String::class.java
        )
        loginPresenter.validateLoginDetails(EMPTY_EMPLOYEE_ID, PASSWORD)
        Mockito.verify(passwordView).showEmptyEmployeeIdError()
        ac.allValues
        Truth.assertThat(ac.value).isEmpty()
    }

    @Test
    fun validateLoginDetails_fail_emptyPassword(){
        val ac = ArgumentCaptor.forClass(
            String::class.java
        )
        loginPresenter.validateLoginDetails(EMPLOYEE_ID, EMPTY_PASSWORD)
        Mockito.verify(passwordView).showEmptyPasswordError()
        ac.allValues
        Truth.assertThat(ac.value).isEmpty()
    }

    @Test
    fun validateLoginDetails_fail_invalidPassword(){
        val ac = ArgumentCaptor.forClass(
            String::class.java
        )
        loginPresenter.validateLoginDetails(EMPLOYEE_ID, INVALID_PASSWORD)
        Mockito.verify(passwordView).showInvalidPasswordError()
        ac.allValues
        Truth.assertThat(ac.value).isEmpty()
    }

    @Test
    fun validateLoginDetails_pass_validData(){
        Mockito.`when`(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN)).thenReturn("ABC")
        val ac = ArgumentCaptor.forClass(
            String::class.java
        )
        loginPresenter.validateLoginDetails(EMPLOYEE_ID, PASSWORD)
        Mockito.verify(listner1).onRegistrationTakenSaved(ac.capture(), ac.capture())
        val list: List<String> = ac.allValues
        Truth.assertThat(list[0]).isEqualTo(EMPLOYEE_ID)
        Truth.assertThat(list[1]).isEqualTo(PASSWORD)
    }
}