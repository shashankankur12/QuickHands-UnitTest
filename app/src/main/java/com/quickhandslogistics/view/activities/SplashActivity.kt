package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.CrashlyticsRegistrar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.core.CrashlyticsCore
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils

class SplashActivity : AppCompatActivity() {

    lateinit var mSplashHandler: Runnable
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Utils.changeStatusBar(this)

        mSplashHandler = Runnable {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
            finish()
        }

        Handler().postDelayed(
            mSplashHandler, 2000
        )
    }
}
