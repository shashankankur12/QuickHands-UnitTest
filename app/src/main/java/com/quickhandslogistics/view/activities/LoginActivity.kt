package com.quickhandslogistics.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Utils.changeStatusBar(this)

        text_forgot_password.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }

        button_login.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }

    }
}
