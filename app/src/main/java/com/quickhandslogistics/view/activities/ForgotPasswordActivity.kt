package com.quickhandslogistics.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.layout_header.*
import okhttp3.internal.Util

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        Utils.changeStatusBar(this)

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        button_next.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }
    }
}
