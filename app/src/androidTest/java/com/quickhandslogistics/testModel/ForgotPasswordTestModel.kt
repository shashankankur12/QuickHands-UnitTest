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

class ForgotPasswordTestModel(private val context: Context, private val fetchingIdlingResource: FetchingIdlingResource) {

    private val editTextEmpId = Espresso.onView(ViewMatchers.withId(R.id.editTextEmpId))
    private val buttonPasswordReset = Espresso.onView(ViewMatchers.withId(R.id.buttonPasswordReset))

    fun emptyEmployeeId() {
        clearText(editTextEmpId)
        click(buttonPasswordReset)
        checkTextVisible(context.getString(R.string.empty_employee_id_message))
    }

    fun loginWithCorrectCredentials() {
        replaceText(editTextEmpId, AppTestConstants.LOGIN_EMPLOYEE_ID)

        IdlingRegistry.getInstance().register(fetchingIdlingResource)
        click(buttonPasswordReset)
        checkTextVisible(context.getString(R.string.success))
        IdlingRegistry.getInstance().unregister(fetchingIdlingResource)
    }
}