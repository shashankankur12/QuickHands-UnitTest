package com.quickhandslogistics.modified.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.SharedPref
import kotlinx.android.synthetic.main.layout_toolbar.*

open class BaseActivity : AppCompatActivity() {

    protected lateinit var activity: Activity
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    protected lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sharedPref = SharedPref.getInstance()
    }

    protected fun configStatusBar() {
        val window = activity.window
        val decor = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(activity, android.R.color.white)
        } else {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(activity, R.color.colorLightGrey)
        }
    }

    protected fun setupToolbar(title: String) {
        toolbar.title = ""
        setSupportActionBar(toolbar)

        textViewTitle.text = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected fun startIntent(
        className: Class<*>,
        bundle: Bundle? = null,
        isFinish: Boolean = false,
        flags: Array<Int>? = null
    ) {
        val intent = Intent(this, className)
        flags?.let {
            for (flag in flags) {
                intent.addFlags(flag)
            }
        }
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        if (isFinish) finish()
        overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
    }

    protected fun startShakeAnimation(view: View) {
        val animatorSet = AnimatorSet()

        val object1: ObjectAnimator =
            ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.75f, 1.75f, 1.75f, 1f)
        val object2: ObjectAnimator =
            ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.75f, 1.75f, 0.85f, 1f)

        animatorSet.playTogether(object1, object2)
        animatorSet.start()
    }
}