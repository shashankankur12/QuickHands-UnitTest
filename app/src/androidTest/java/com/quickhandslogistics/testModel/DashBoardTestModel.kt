package com.quickhandslogistics.testModel

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.quickhandslogistics.R
import com.quickhandslogistics.resources.FetchingIdlingResource

class DashBoardTestModel(private val context: Context, private val fetchingIdlingResource: FetchingIdlingResource) {

    private val drawerLayout = Espresso.onView(withId(R.id.drawerLayout))
    private val actionCancelAllWork = Espresso.onView(withText(R.string.cancel_all_work_schedules))

    fun isNavigationDrawerWorking() {
        drawerLayout.check(matches(isClosed())).perform(DrawerActions.open());
    }

    fun isMenuOptionWorking() {
        openActionBarOverflowOrOptionsMenu(context)
        actionCancelAllWork.perform(ViewActions.click())
    }
}