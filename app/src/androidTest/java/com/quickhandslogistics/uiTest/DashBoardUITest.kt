package com.quickhandslogistics.uiTest

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.quickhandslogistics.resources.FetchingIdlingResource
import com.quickhandslogistics.testModel.DashBoardTestModel
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.DashBoardActivity
import com.quickhandslogistics.views.workSheet.AllWorkScheduleCancelActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashBoardUITest : BaseUITest() {

    private lateinit var dashBoardTestModel: DashBoardTestModel
    private var activity: BaseActivity? = null

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(DashBoardActivity::class.java)

    private lateinit var fetchingIdlingResource: FetchingIdlingResource

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        activity = getActivity(activityScenarioRule) as BaseActivity?

        // Get OkHttp Dispatcher to check when API call finishes.
        val okHttpClientDispatcher = activity?.getOkHttpClientDispatcher()
        fetchingIdlingResource = FetchingIdlingResource(okHttpClientDispatcher)

        dashBoardTestModel = DashBoardTestModel(context, fetchingIdlingResource)
        Intents.init()
    }

    @Test
    fun testIsNavigationDrawerWorking() {
        dashBoardTestModel.isNavigationDrawerWorking()
    }

    @Test
    fun testIsMenuOptionWorking() {
        dashBoardTestModel.isMenuOptionWorking()

        Intents.intended(IntentMatchers.hasComponent(AllWorkScheduleCancelActivity::class.java.name))
    }

    @After
    fun clear() {
        Intents.release()
    }
}