package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import co.clicke.databases.SharedPreferenceHandler
import com.fileutils.mainTest
import com.quickhandslogistics.R
import com.quickhandslogistics.model.login.Data
import com.quickhandslogistics.model.login.LoginRequest
import com.quickhandslogistics.model.login.LoginResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.session.SessionManager
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity : BaseActivity(), AppConstant {

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
            Utils.hideSoftKeyboard(this)

            var employeeId = edit_employee_id.text.toString().trim()
            var password = edit_password.text.toString().trim()
            val loginRequest = LoginRequest(employeeId, password)

            if (validateForm(loginRequest)) {
                getLogin(loginRequest)
            }
           // validateForm(employeeId,password)
        }

        text_forgot_password.setOnClickListener {

            Utils.showForgotPasswordDialog(R.style.dialogAnimation, getString(R.string.string_request_message),this )
        }
    }


    override fun onResume() {
        super.onResume()
        mainTest()
    }

    fun setLanguageData(language: String?) {
        SharedPreferenceHandler.setLanguageSelected(language)
        LanguageManager.setLanguage(this, language)
    }


    private fun validateForm(loginrequest: LoginRequest): Boolean {
        val employeeId = loginrequest.id
        val password = loginrequest.password

        when {
            TextUtils.isEmpty(employeeId) -> {
                Utils.Shake(edit_employee_id)
                SnackBarFactory.createSnackBar(
                    this,
                    scroll_top,
                    resources.getString(R.string.text_employee_error_msg)
                )
                return false
            }

            TextUtils.isEmpty(password) -> {
                Utils.Shake(edit_password)
                SnackBarFactory.createSnackBar(
                    this,
                    scroll_top,
                    resources.getString(R.string.text_password_err_msg)
                )
                return false
            }

            password.length < 8 -> {
                Utils.Shake(edit_password)
                SnackBarFactory.createSnackBar(
                    this,
                    scroll_top,
                    resources.getString(R.string.text_password_err_length)
                )
                return false
            }
        }
        return true
    }

    private fun getLogin(loginrequest: LoginRequest) {
        val dialog = CustomProgressBar.getInstance(this).showProgressDialog("Logging in...")
        DataManager.doLogin(this, loginrequest, object : ResponseListener<LoginResponse> {
            override fun onSuccess(response: LoginResponse) {
                dialog.dismiss()
                if (response.data == null) return

                dialog.dismiss()
                val loginData = response.data.get(0)
                saveUserData(loginData)
            }

            override fun onError(error: Any) {
                dialog.dismiss()
                Utils.showError(this@LoginActivity, scroll_top, error)
            }
        })
    }


    private fun saveUserData(loginData: Data) {
        SessionManager.setSession(loginData)

        navigateActivity()
    }

    private fun navigateActivity() {
        startActivity(
            Intent(this, DashboardActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        finish()
    }
}
