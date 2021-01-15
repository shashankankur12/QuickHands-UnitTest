package com.quickhandslogistics.controls

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import com.quickhandslogistics.R

class MaxHeightNestedScrollView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private var maxHeight: Float

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightNestedScrollView)
        maxHeight = attributes.getDimension(R.styleable.MaxHeightNestedScrollView_maxHeight, 0F)
        attributes.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (maxHeight.toInt() != 0) {
            val height = MeasureSpec.makeMeasureSpec(maxHeight.toInt(), MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}