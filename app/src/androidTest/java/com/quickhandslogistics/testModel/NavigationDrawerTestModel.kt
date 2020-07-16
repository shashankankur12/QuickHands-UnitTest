package com.quickhandslogistics.testModel

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.ViewMatchers.*
import com.quickhandslogistics.R
import com.quickhandslogistics.resources.FetchingIdlingResource
import com.quickhandslogistics.utils.CustomScrollActions
import com.quickhandslogistics.utils.ViewUtils.checkViewVisible
import org.hamcrest.Matchers.allOf

class NavigationDrawerTestModel(private val context: Context, private val fetchingIdlingResource: FetchingIdlingResource) {

    private val drawerLayout = onView(withId(R.id.drawerLayout))
    private val nestedScrollView = onView(withId(R.id.nestedScrollView))
    private var textViewWorkSheet = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.today_s_work_sheet)))))
    private var textViewScheduleLumperTime = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.schedule_lumpers_time)))))
    private var textViewSchedule = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.schedule)))))
    private var textViewLumpers = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.lumper_contact)))))
    private var textViewLumperSheet = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.l_sheet)))))
    private var textViewCustomerSheet = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.customer_sheet)))))
    private var textViewReports = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.reports)))))
    private var textViewSettings = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.settings)))))
    private var textViewLogout = onView(allOf(withId(R.id.list_item_nav_drawer_text), hasSibling(withText(context.getString(R.string.logout)))))
    private val textViewVersionName = onView(withId(R.id.textViewVersionName))

    private fun openDrawer() {
        drawerLayout.check(matches(isClosed())).perform(DrawerActions.open());
    }

    fun isAllOptionsAvailable() {
        openDrawer()

        checkViewVisible(textViewWorkSheet, isSwipe = true)
        checkViewVisible(textViewScheduleLumperTime, isSwipe = true)
        checkViewVisible(textViewSchedule, isSwipe = true)
        checkViewVisible(textViewLumpers, isSwipe = true)
        checkViewVisible(textViewLumperSheet, isSwipe = true)
        checkViewVisible(textViewCustomerSheet, isSwipe = true)
        checkViewVisible(textViewReports, isSwipe = true)
        checkViewVisible(textViewSettings, isSwipe = true)
        checkViewVisible(textViewLogout, isSwipe = true)
        checkViewVisible(textViewVersionName)
    }

    fun checkAppVersionValue() {
        openDrawer()

        textViewVersionName.perform(CustomScrollActions.nestedScrollTo())
            .check(matches(isCompletelyDisplayed()))

//        scroll(nestedScrollView)
//        validateSubText(textViewVersionName, "v"+BuildConfig.VERSION_NAME)
    }
}