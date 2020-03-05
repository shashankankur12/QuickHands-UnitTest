package com.quickhandslogistics.view.activities


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.fileutils.mainTest
import com.quickhandslogistics.R
import com.quickhandslogistics.model.login.Data
import com.quickhandslogistics.model.login.LoginRequest
import com.quickhandslogistics.model.login.LoginResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_AUTH_TOKEN
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_EMAIL
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_EMP_ID
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_IS_ACTIVE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_ROLE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERFIRSTNAME
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERLASTNAME
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERPHONE
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USER_NAME
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : BaseActivity(), AppConstant {
    var empId  : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Utils.changeStatusBar(this)

        setEmpId()
        edit_password.transformationMethod = PasswordTransformationMethod()

        text_forgot_password.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }

        button_login.setOnClickListener {
            Utils.hideSoftKeyboard(this)

            var employeeLoginId = edit_employee_login_id.text.toString().trim()
            var password = edit_password.text.toString().trim()
            val loginRequest = LoginRequest(employeeLoginId, password)

            if (validateForm(loginRequest)) {
                getLogin(loginRequest)
            }
        }

        text_forgot_password.setOnClickListener {

            Utils.showForgotPasswordDialog(R.style.dialogAnimation, getString(R.string.string_request_message),this )
        }
    }

    override fun onResume() {
        super.onResume()
        setEmpId()
    }

    fun setLanguageData(language: String?) {

        LanguageManager.setLanguage(this, language)
    }

    private fun validateForm(loginrequest: LoginRequest): Boolean {
        val employeeEmailId = loginrequest.id
        val password = loginrequest.password

        when {

            TextUtils.isEmpty(employeeEmailId) -> {
                Utils.Shake(edit_employee_login_id)
                SnackBarFactory.createSnackBar(this, scroll_top, resources.getString(R.string.text_employee_error_msg))
                return false
            }

          /*  !validateEmail(employeeEmailId) -> {
                Utils.Shake(edit_employee_login_id)
                SnackBarFactory.createSnackBar(this, scroll_top, resources.getString(R.string.text_employee_valid_email_error_msg))
                return false
            }*/

            TextUtils.isEmpty(password) -> {
                Utils.Shake(edit_password)
                SnackBarFactory.createSnackBar(this, scroll_top, resources.getString(R.string.text_password_err_msg))
                return false
            }

            password.length < 2 -> {
                Utils.Shake(edit_password)
                SnackBarFactory.createSnackBar(this, scroll_top, resources.getString(R.string.text_password_err_length))
                return false
            }
        }
        return true
    }

   private fun  getLogin(loginrequest: LoginRequest) {
           val dialog = CustomProgressBar.getInstance(this@LoginActivity).showProgressDialog("Logging in...")

              DataManager.doLogin( loginrequest, object : ResponseListener<LoginResponse> {
                      override fun onSuccess(response: LoginResponse) {
                            dialog.dismiss()
                          if (response.success) {

                              val loginData = response.data
                              saveUserData(loginData)
                              Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                          } else Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                      }

                      override fun onError(error: Any) {
                          dialog.dismiss()
                          Utils.Shake(edit_employee_login_id)
                          Utils.Shake(edit_password)
                          SnackBarFactory.createSnackBar(this@LoginActivity, scroll_top, resources.getString(R.string.invalid_email_password))
                      }
                  })
          }

    private fun saveUserData(loginData: Data) {

                SharedPref.getInstance().setBoolean(PREF_IS_ACTIVE,loginData.isActive)
                SharedPref.getInstance().setString(PREF_AUTH_TOKEN, loginData.token)
                SharedPref.getInstance().setString(PREF_EMP_ID,loginData.employeeId)
                SharedPref.getInstance().setString(PREF_USERPHONE,loginData.phone)
                SharedPref.getInstance().setString(PREF_EMAIL,loginData.email)
                SharedPref.getInstance().setString(PREF_ROLE,loginData.role)
                SharedPref.getInstance().setString(PREF_USER_NAME,loginData.firstName + " " + loginData.lastName)
                SharedPref.getInstance().setString(PREF_USERFIRSTNAME,loginData.firstName)
                SharedPref.getInstance().setString(PREF_USERLASTNAME,loginData.lastName)

            navigateActivity()
    }

    private fun navigateActivity() {

        startActivity(Intent(this, DashboardActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        finish()
    }

    fun validateEmail(email: String): Boolean {
        val pattern = Pattern.compile(AppConstant.EMAIL_REGEX, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun setEmpId(){

        if(!TextUtils.isEmpty(SharedPref.getInstance().getString(PREF_EMP_ID))) {
            empId = SharedPref.getInstance().getString(PREF_EMP_ID)
            edit_employee_login_id.setText(empId)
            edit_employee_login_id.setSelection(empId.length)
        }
    }
}