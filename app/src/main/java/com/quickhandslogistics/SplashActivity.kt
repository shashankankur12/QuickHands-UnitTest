package com.quickhandslogistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quickhandslogistics.utils.Utils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Utils.changeStatusBar(this)
    }
}
