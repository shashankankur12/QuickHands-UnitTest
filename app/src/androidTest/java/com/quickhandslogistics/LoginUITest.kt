package com.quickhandslogistics

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.LoginView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginUITest {

    private lateinit var loginView: LoginView

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        loginView = LoginView(context)
    }

    @Test
    fun testEmptyEmployeeId() {
        loginView.emptyEmployeeId()
    }
}