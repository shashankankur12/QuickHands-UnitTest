package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.StringUtils
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

            var email = edit_email.text.toString().trim()
            var password = edit_password.text.toString().trim()

            validateForm(email, password)
        }

    }

    private fun validateForm(email : String, password : String) {

        when {
            TextUtils.isEmpty(email) -> {
                text_input_email.error = "Email cannot be empty"
            }

            !StringUtils.isValidEmailId(email) -> {
                text_input_email.error = "Email should be valid"
            }

            TextUtils.isEmpty(password) -> {
                text_input_password.error = "Password cannot be empty"
                text_input_email.error = null
            }

            password.length < 8 -> {
                text_input_password.error = "Password should contain atleast 8 characters"
            }

            else -> {

                text_input_email.error = null
                text_input_password.error = null

                startActivity(Intent(this, DashboardActivity::class.java))
                overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
            }
        }
    }
}
