package com.quickhandslogistics.modified.views

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SharedPref

open class BaseFragment : Fragment() {

    var fragmentActivity: FragmentActivity? = null
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    protected lateinit var sharedPref: SharedPref
    private var progressDialog: CustomProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let { activity ->
            fragmentActivity = activity
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
            progressDialog = CustomProgressBar.getInstance(activity)
        }
        sharedPref = SharedPref.getInstance()
    }

    protected fun startIntent(
        className: Class<*>,
        bundle: Bundle? = null,
        isFinish: Boolean = false,
        flags: Array<Int>? = null,
        requestCode: Int? = null
    ) {
        val intent = Intent(fragmentActivity, className)
        flags?.let {
            for (flag in flags) {
                intent.addFlags(flag)
            }
        }
        bundle?.let {
            intent.putExtras(bundle)
        }
        fragmentActivity?.let { fragmentActivity ->
            requestCode?.also {
                startActivityForResult(intent, requestCode)
            } ?: run {
                startActivity(intent)
            }
            if (isFinish) fragmentActivity.finish()
            fragmentActivity.overridePendingTransition(
                R.anim.anim_next_slide_in,
                R.anim.anim_next_slide_out
            )
        }
    }

    fun hideProgressDialog() {
        progressDialog?.hideProgressDialog()
    }

    fun showProgressDialog(message: String) {
        progressDialog?.showProgressDialog(message)
    }
}