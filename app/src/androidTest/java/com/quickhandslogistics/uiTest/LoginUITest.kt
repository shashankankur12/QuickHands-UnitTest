package com.quickhandslogistics.uiTest

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.quickhandslogistics.resources.FetchingIdlingResource
import com.quickhandslogistics.testModel.LoginTestModel
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.DashBoardActivity
import com.quickhandslogistics.views.ForgotPasswordActivity
import com.quickhandslogistics.views.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginUITest : BaseUITest() {

    private lateinit var loginTestModel: LoginTestModel
    private var activity: BaseActivity? = null

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    private lateinit var fetchingIdlingResource: FetchingIdlingResource

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        activity = getActivity(activityScenarioRule) as BaseActivity?

        // Get OkHttp Dispatcher to check when API call finishes.
        val okHttpClientDispatcher = activity?.getOkHttpClientDispatcher()
        fetchingIdlingResource = FetchingIdlingResource(okHttpClientDispatcher)

        loginTestModel = LoginTestModel(context, fetchingIdlingResource)
        Intents.init()
    }

    @Test
    fun testEmptyEmployeeId() {
        loginTestModel.emptyEmployeeId()
    }

    @Test
    fun testEmptyPassword() {
        loginTestModel.emptyPassword()
    }

    @Test
    fun testShortPassword() {
        loginTestModel.shortPassword()
    }

    @Test
    fun testEmptyEmployeeIdPassword() {
        loginTestModel.emptyEmployeeIdPassword()
    }

    @Test
    fun testForgotPasswordNavigation() {
        loginTestModel.forgotPasswordNavigation()

        Intents.intended(IntentMatchers.hasComponent(ForgotPasswordActivity::class.java.name))
    }

    @Test
    fun testLoginWithWrongCredentials() {
        loginTestModel.loginWithWrongCredentials()
    }

    @Test
    fun testLoginWithCorrectCredentials() {
        loginTestModel.loginWithCorrectCredentials()

        Intents.intended(IntentMatchers.hasComponent(DashBoardActivity::class.java.name))
    }

    @After
    fun clear() {
        Intents.release()
    }
}