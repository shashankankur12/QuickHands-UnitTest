package com.quickhandslogistics.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.quickhandslogistics.R;

public class PermissionUtil {
    public static final int PERMISSION_REQUEST_CODE = 1;

    public static boolean checkStorage(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStorage(Activity activity) {
        requestStorage(activity, null);
    }

    private static void requestStorage(Activity activity, Fragment fragment) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            CustomProgressBar.Companion.getInstance().showErrorDialog(activity.getString(R.string.use_external_storage_alert_message), activity);
        } else {
            if (fragment == null) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    public static boolean granted(int[] grantResults) {
        boolean ret = true;

        // If nothing was granted, return false,
        if (grantResults.length == 0) {
            ret = false;
        }

        // If any of the grant results were false, then return false.
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                ret = false;
                break;
            }
        }

        return ret;
    }
}
