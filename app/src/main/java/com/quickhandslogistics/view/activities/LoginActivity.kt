package com.quickhandslogistics.view.activities

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import co.clicke.databases.SharedPreferenceHandler
import com.fileutils.testMain
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.DialogHelper
import com.quickhandslogistics.utils.LanguageManager
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_login.*
import render.animations.Attention
import render.animations.Bounce
import render.animations.Render

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Utils.changeStatusBar(this)

        edit_password.transformationMethod = PasswordTransformationMethod()

        text_forgot_password.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }

        button_login.setOnClickListener {

            var employeeId = edit_employee_id.text.toString().trim()
            var password = edit_password.text.toString().trim()

            validateForm(employeeId,password)
        }

        text_forgot_password.setOnClickListener {

            Utils.showForgotPasswordDialog(R.style.dialogAnimation, getString(R.string.string_request_message),this )
        }
    }


    override fun onResume() {
        super.onResume()
        testMain()
    }

    fun setLanguageData(language: String?) {
        SharedPreferenceHandler.setLanguageSelected(language)
        LanguageManager.setLanguage(this, language)
    }


    private fun validateForm(employeeId : String, password : String) {

        when {
            TextUtils.isEmpty(employeeId) -> {
                Utils.Shake(edit_employee_id)
                //render.start()
                text_input_email.error = resources.getString(R.string.text_employee_error_msg)

            }

            /*!StringUtils.isValidEmailId(email) -> {
                text_input_email.error = "Email should be valid"
            }*/

            TextUtils.isEmpty(password) -> {
                Utils.Shake(edit_password)
                text_input_password.error = resources.getString(R.string.text_password_err_msg)
                text_input_email.error = null
            }

            password.length < 8 -> {
                Utils.Shake(edit_password)
                text_input_password.error = resources.getString(R.string.text_password_err_length)
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
