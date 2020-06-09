package com.quickhandslogistics.uiTest

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.quickhandslogistics.resources.FetchingIdlingResource
import com.quickhandslogistics.testModel.ForgotPasswordTestModel
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.DashBoardActivity
import com.quickhandslogistics.views.ForgotPasswordActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForgotPasswordUITest : BaseUITest() {

    private lateinit var forgotPasswordTestModel: ForgotPasswordTestModel
    private var activity: BaseActivity? = null

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(ForgotPasswordActivity::class.java)

    private lateinit var fetchingIdlingResource: FetchingIdlingResource

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        activity = getActivity(activityScenarioRule) as BaseActivity?

        // Get OkHttp Dispatcher to check when API call finishes.
        val okHttpClientDispatcher = activity?.getOkHttpClientDispatcher()
        fetchingIdlingResource = FetchingIdlingResource(okHttpClientDispatcher)

        forgotPasswordTestModel = ForgotPasswordTestModel(context, fetchingIdlingResource)
        Intents.init()
    }

    @Test
    fun testEmptyEmployeeId() {
        forgotPasswordTestModel.emptyEmployeeId()
    }

    @Test
    fun testForgotPassword() {
        forgotPasswordTestModel.loginWithCorrectCredentials()
    }

    @After
    fun clear() {
        Intents.release()
    }
}