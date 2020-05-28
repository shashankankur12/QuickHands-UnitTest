package com.quickhandslogistics.views

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import com.quickhandslogistics.AppTestConstants
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.ViewUtils

class LoginView(private val context: Context) {

    private val editTextEmployeeId = Espresso.onView(ViewMatchers.withId(R.id.editTextEmployeeId))
    private val editTextPassword = Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
    private val buttonLogin = Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))

    fun emptyEmployeeId() {
        ViewUtils.clearText(editTextEmployeeId)
        ViewUtils.replaceText(editTextPassword, AppTestConstants.LOGIN_PASSWORD)
        ViewUtils.click(buttonLogin)
    }
}