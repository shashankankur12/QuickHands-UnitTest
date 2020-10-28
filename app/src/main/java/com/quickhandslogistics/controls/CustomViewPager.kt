package com.quickhandslogistics.controls

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var pagingEnabled: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.pagingEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.pagingEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }
}