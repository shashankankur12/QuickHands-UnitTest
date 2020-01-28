package com.quickhandslogistics.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LanguageManager {

    public enum Languages {
        ENG("en-us"),
        ES("es");
        private String language;

        Languages(String lang) {
            language = lang;
        }
        @Override
        public String toString() {
            return language;
        }
    }

    public static void setLanguage(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
    }
}
