package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppPreference
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private val SCREEN_TIMEOUT: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val anim =  AnimationUtils.loadAnimation(this, R.anim.anim_slide_up)
        anim.duration = 700
        image_splash_logo.startAnimation(anim)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Utils.changeStatusBar(this)
        isLogin()

    }

    private fun isLogin() {
        val handler = Handler()
        if (AppPreference.getInstance(this).getBoolean(AppConstant.PREF_IS_ACTIVE)) {
            handler.postDelayed({
                startActivity(Intent(this, DashboardActivity::class.java))
                overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
                finish()
            }, SCREEN_TIMEOUT)
        } else {
            handler.postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
                finish()
            }, SCREEN_TIMEOUT)
        }
    }
}
