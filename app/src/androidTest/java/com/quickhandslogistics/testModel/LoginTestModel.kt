package com.quickhandslogistics.testModel

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.matcher.ViewMatchers
import com.quickhandslogistics.R
import com.quickhandslogistics.resources.FetchingIdlingResource
import com.quickhandslogistics.utils.AppTestConstants
import com.quickhandslogistics.utils.ViewUtils.checkTextVisible
import com.quickhandslogistics.utils.ViewUtils.clearText
import com.quickhandslogistics.utils.ViewUtils.click
import com.quickhandslogistics.utils.ViewUtils.replaceText

class LoginTestModel(private val context: Context, private val fetchingIdlingResource: FetchingIdlingResource) {

    private val editTextEmployeeId = Espresso.onView(ViewMatchers.withId(R.id.editTextEmployeeId))
    private val editTextPassword = Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
    private val buttonLogin = Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
    private val textViewForgotPassword = Espresso.onView(ViewMatchers.withId(R.id.textViewForgotPassword))

    private val snackBarTextId = (com.google.android.material.R.id.snackbar_text)
    private val snackBarText = Espresso.onView(ViewMatchers.withId(snackBarTextId))

    fun emptyEmployeeId() {
        clearText(editTextEmployeeId)
        replaceText(editTextPassword, AppTestConstants.LOGIN_PASSWORD)
        click(buttonLogin)
        checkTextVisible(context.getString(R.string.empty_employee_id_message))
    }

    fun emptyPassword() {
        replaceText(editTextEmployeeId, AppTestConstants.LOGIN_EMPLOYEE_ID)
        clearText(editTextPassword)
        click(buttonLogin)
        checkTextVisible(context.getString(R.string.empty_password_message))
    }

    fun shortPassword() {
        replaceText(editTextEmployeeId, AppTestConstants.LOGIN_EMPLOYEE_ID)
        replaceText(editTextPassword, AppTestConstants.LOGIN_SHORT_PASSWORD)
        click(buttonLogin)
        checkTextVisible(context.getString(R.string.password_short_length_message))
    }

    fun emptyEmployeeIdPassword() {
        clearText(editTextEmployeeId)
        clearText(editTextPassword)
        click(buttonLogin)
        checkTextVisible(context.getString(R.string.empty_employee_id_message))
    }

    fun forgotPasswordNavigation() {
        click(textViewForgotPassword)
    }

    fun loginWithWrongCredentials() {
        replaceText(editTextEmployeeId, AppTestConstants.LOGIN_WRONG_EMPLOYEE_ID)
        replaceText(editTextPassword, AppTestConstants.LOGIN_WRONG_PASSWORD)

        IdlingRegistry.getInstance().register(fetchingIdlingResource)
        click(buttonLogin)
        checkTextVisible("Invalid employee ID or password")
        IdlingRegistry.getInstance().unregister(fetchingIdlingResource)
    }

    fun loginWithCorrectCredentials() {
        replaceText(editTextEmployeeId, AppTestConstants.LOGIN_EMPLOYEE_ID)
        replaceText(editTextPassword, AppTestConstants.LOGIN_PASSWORD)

        IdlingRegistry.getInstance().register(fetchingIdlingResource)
        click(buttonLogin)
        IdlingRegistry.getInstance().unregister(fetchingIdlingResource)
    }

}