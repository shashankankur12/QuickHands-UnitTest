package com.quickhandslogistics.modified.views

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SharedPref
import kotlinx.android.synthetic.main.layout_toolbar.*

open class BaseActivity : AppCompatActivity(), BaseContract.View {

    lateinit var sharedPref: SharedPref

    protected lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        sharedPref = SharedPref.getInstance()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
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
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }
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

    /** Presenter Listeners */
    override fun showProgressDialog(message: String) {
        CustomProgressBar.getInstance().show(message = message, activityContext = activity)
    }

    override fun hideProgressDialog() {
        CustomProgressBar.getInstance().hide()
    }
}