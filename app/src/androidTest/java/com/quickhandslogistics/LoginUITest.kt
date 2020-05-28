package com.quickhandslogistics

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.quickhandslogistics.views.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginUITest {

    @Rule
    private val activityTestRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java, true, false)

    @Test
    fun emptyEmployeeId() {
        activityTestRule.launchActivity(Intent())
    }
}