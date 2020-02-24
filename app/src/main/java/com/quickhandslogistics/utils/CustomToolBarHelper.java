package com.quickhandslogistics.utils;

import android.app.Activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.quickhandslogistics.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomToolBarHelper {

    @BindView(R.id.imageView)
    ImageView imageLogo;
    @BindView(R.id.text_title)
    TextView textTitle;
    private Activity activity;

    public CustomToolBarHelper(@NonNull View view) {
        ButterKnife.bind(this,view);
    }

    public CustomToolBarHelper(@NonNull Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        ButterKnife.bind(this,view);
        this.activity = activity;
    }

    public void setTitle(String title){
        if(textTitle == null) return;
        textTitle.setText(title);
    }

    public void setTitle(@StringRes int title){
        if(textTitle == null) return;
        textTitle.setText(title);
    }

    public void setLogoIcon(@DrawableRes int resId){
        if(imageLogo == null) return;
        imageLogo.setImageResource(resId);
    }


    public void enableBackPress(){
        setLogoIcon(R.drawable.ic_back);
        goBack();
    }

    private void goBack(){
        if(activity == null) return;
        imageLogo.setOnClickListener(v -> activity.finish());
    }
}
