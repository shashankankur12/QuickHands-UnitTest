package com.quickhandslogistics.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SharedPref
import kotlinx.android.synthetic.main.layout_toolbar.*
import okhttp3.Dispatcher


open class BaseActivity : AppCompatActivity(), BaseContract.View {

    lateinit var sharedPref: SharedPref
    private var isDataSave=true

    protected lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        sharedPref = SharedPref.getInstance()
    }


    override fun onBackPressed() {
        if (isDataSave) {
            super.onBackPressed()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        } else {
            showLeavePopup()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(context: Context?) {
        val newBase = LocaleChanger.configureBaseContext(context)
        super.attachBaseContext(newBase)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
    }

    override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(this)
    }

    override fun onDestroy() {
        ActivityRecreationHelper.onDestroy(this)
        super.onDestroy()
    }

    protected fun setupToolbar(title: String = "", showBackButton: Boolean = true) {
        toolbar.title = title
        setSupportActionBar(toolbar)

        if (showBackButton) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            if (title.equals(getString(R.string.my_profile)) || title.equals(getString(R.string.lumper_profile)))
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white)
            else if(title.equals(getString(R.string.lumper_contact))){
                val background = BitmapDrawable(BitmapFactory.decodeResource(resources, R.drawable.header_background))
                background.tileModeX = Shader.TileMode.REPEAT
                supportActionBar?.setBackgroundDrawable(background)
            }else supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }
    }

    protected fun isDataSave(isDataSave: Boolean) {
       this.isDataSave= isDataSave
    }

    fun startIntent(className: Class<*>, bundle: Bundle? = null, isFinish: Boolean = false, flags: Array<Int>? = null, requestCode: Int? = null) {
        val intent = Intent(this, className)
        flags?.let {
            for (flag in flags) {
                intent.addFlags(flag)
            }
        }
        bundle?.let {
            intent.putExtras(bundle)
        }

        requestCode?.also {
            startActivityForResult(intent, requestCode)
        } ?: run {
            startActivity(intent)
        }
        if (isFinish) finish()
        overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
    }

    fun startZoomIntent(className: Class<*>, bundle: Bundle, view: View) {
        val intent = Intent(this, className)
        intent.putExtras(bundle)

        val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, view, getString(R.string.transition_name))
        startActivity(intent, transitionActivityOptions.toBundle())
    }

    fun showErrorDialog(message: String) {
        CustomProgressBar.getInstance().showErrorDialog(message, activity)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun addNotesTouchListener(editText: EditText) {
        editText.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }

    private fun showLeavePopup() {
        CustomProgressBar.getInstance().showLeaveDialog(
            getString(R.string.discard_leave_alert_message),
            activity,
            object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    isDataSave=true
                    onBackPressed()
                }
                override fun onCancelClick() {
                }
            })
    }

    /** Presenter Listeners */
    override fun showProgressDialog(message: String) {
        CustomProgressBar.getInstance().show(message = message, activityContext = activity)
    }

    override fun hideProgressDialog() {
        CustomProgressBar.getInstance().hide()
    }

    /** Espresso Test Methods */
    fun getOkHttpClientDispatcher(): Dispatcher? {
        return DataManager.getOkHttpClient()?.dispatcher
    }
}