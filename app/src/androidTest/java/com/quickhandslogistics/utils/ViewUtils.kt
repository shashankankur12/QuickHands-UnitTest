package com.quickhandslogistics.utils

import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

object ViewUtils {

    fun clearText(view: ViewInteraction) {
        view.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        view.perform(ViewActions.clearText(), ViewActions.closeSoftKeyboard())
    }

    fun replaceText(view: ViewInteraction, text: String) {
        view.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        view.perform(ViewActions.replaceText(text), ViewActions.closeSoftKeyboard())
    }

    fun click(view: ViewInteraction) {
        view.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        view.perform(ViewActions.click())
    }
}