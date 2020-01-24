package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    lateinit var mSplashHandler: Runnable
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

       /* val anim = RotateAnimation(0f, 350f, 15f, 15f)
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = Animation.ABSOLUTE
        anim.duration = 700*/

       val anim =  AnimationUtils.loadAnimation(this, R.anim.anim_slide_up)
        anim.duration = 700
        image_splash_logo.startAnimation(anim)


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
