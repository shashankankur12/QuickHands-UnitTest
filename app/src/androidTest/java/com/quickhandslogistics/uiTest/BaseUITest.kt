package com.quickhandslogistics.uiTest

import android.app.Activity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class BaseUITest {

    protected fun getActivity(activityScenarioRule: ActivityScenarioRule<*>): Activity? {
        var activity: Activity? = null
        activityScenarioRule.scenario.onActivity {
            activity = it
        }
        return activity
    }
}