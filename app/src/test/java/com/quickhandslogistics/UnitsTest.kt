package com.quickhandslogistics

import android.content.Context
import android.content.res.Resources
import com.google.common.truth.Truth.assertThat
import com.quickhandslogistics.contracts.LoginContract
import com.quickhandslogistics.presenters.LoginPresenter
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.views.LoginActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class UnitsTest {
    @Mock
    lateinit var loginView: LoginContract.View
    @Mock
    lateinit var resources: Resources
    @Mock
    lateinit var  sharedPref: SharedPref
    @Test
    fun readStringFromContext_LocalizedString() {
        val myObjectUnderTest = LoginPresenter(loginView,resources,sharedPref)

        // ...when the string is returned from the object under test...
        val result: Unit = myObjectUnderTest.validateLoginDetails("user", "user")

        // ...then the result should be the expected one.
        assertThat(result).isEqualTo(FAKE_STRING)
    }

    companion object {
        private const val FAKE_STRING = "Login was successful"
    }
}