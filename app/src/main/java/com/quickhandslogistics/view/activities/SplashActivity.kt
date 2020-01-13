package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils

class SplashActivity : AppCompatActivity() {

    lateinit var mSplashHandler: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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
