package com.quickhandslogistics.modified.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.quickhandslogistics.R;

/**
 * This class creates a TextView which will become invisible when there is no text written in it.
 */
public class CustomTextView extends AppCompatTextView {

    private int emptyVisibility = 0;

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomTextView(Context context) {
        super(context);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        emptyVisibility = attributes.getInt(R.styleable.CustomTextView_emptyDataVisibility, 0);
        attributes.recycle();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (text.toString().isEmpty()) {
            this.setVisibility(emptyVisibility == 0 ? View.INVISIBLE : View.GONE);
        } else {
            this.setVisibility(View.VISIBLE);
        }
    }
}