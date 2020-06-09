package com.quickhandslogistics.utils

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import org.hamcrest.Matchers

object ViewUtils {

    fun clearText(view: ViewInteraction) {
        view.check(matches(isDisplayed()))
        view.perform(ViewActions.clearText(), ViewActions.closeSoftKeyboard())
    }

    fun replaceText(view: ViewInteraction, text: String) {
        view.check(matches(isDisplayed()))
        view.perform(ViewActions.replaceText(text), ViewActions.closeSoftKeyboard())
    }

    fun click(view: ViewInteraction) {
        view.check(matches(isDisplayed()))
        view.perform(ViewActions.click())
    }

    fun checkTextVisible(text: String) {
        val view = Espresso.onView(ViewMatchers.withText(text))
        view.check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    fun checkViewVisible(view: ViewInteraction, isSwipe: Boolean = false) {
        view.check(matches(isDisplayed()))
        if (isSwipe) view.perform(ViewActions.swipeUp())
    }

    fun validateSubText(view: ViewInteraction, text: String) {
        view.check(matches(Matchers.allOf(isDisplayed(), withSubstring(text))))
    }

    fun scroll(scrollableView: ViewInteraction) {
        scrollableView.perform(CustomScrollActions.nestedScrollTo())
    }
}