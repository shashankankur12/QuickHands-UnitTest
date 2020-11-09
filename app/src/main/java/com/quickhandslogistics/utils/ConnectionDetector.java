package com.quickhandslogistics.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.quickhandslogistics.R;

public class ConnectionDetector {
    public static boolean isNetworkConnected(Context mContext) {

        if (mContext == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return (networkInfo != null && networkInfo.isConnected());
    }

    public static Snackbar createSnackBar(Activity activity) {
        Snackbar snackbar  = null;
        if (activity != null) {
             snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(activity, R.color.textWhite));
            snackbar.show();


        }
        return snackbar;
    }
}