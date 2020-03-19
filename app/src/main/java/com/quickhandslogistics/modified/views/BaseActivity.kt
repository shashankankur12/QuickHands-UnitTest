package com.quickhandslogistics.modified.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.controls.NavDrawer
import com.quickhandslogistics.modified.views.fragments.SettingsFragment
import com.quickhandslogistics.modified.views.fragments.lumperSheet.LumperSheetFragment
import com.quickhandslogistics.modified.views.fragments.lumpers.LumpersFragment
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.view.fragments.CustomerSheetFragment
import com.quickhandslogistics.view.fragments.ReportFragment
import kotlinx.android.synthetic.main.layout_toolbar.*

open class BaseActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    lateinit var sharedPref: SharedPref

    protected lateinit var activity: Activity
    private var navDrawer: NavDrawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sharedPref = SharedPref.getInstance()
    }

    protected fun setupToolbar(title: String = "", showBackButton: Boolean = true) {
        toolbar.title = title
        setSupportActionBar(toolbar)

        if (showBackButton) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }
    }

    fun startIntent(
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun setUpNavigationBar() {
        navDrawer = NavDrawer(
            this,
            toolbar,
            supportFragmentManager.beginTransaction()
        )
        navDrawer?.let {
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    ScheduleMainFragment(),
                    getString(R.string.string_schedule),
                    R.drawable.ic_sidemenu_schedule,
                    R.id.include_main_nav_drawer_topItems,
                    true
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    LumpersFragment(),
                    getString(R.string.string_lumper),
                    R.drawable.ic_sidemenu_lumpers,
                    R.id.include_main_nav_drawer_topItems,
                    false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    LumperSheetFragment(),
                    getString(R.string.string_lumper_sheet),
                    R.drawable.ic_sidemenu_lumper_sheet,
                    R.id.include_main_nav_drawer_topItems,
                    false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    CustomerSheetFragment(),
                    getString(R.string.string_customer_sheet),
                    R.drawable.ic_sidemenu_customer_sheet,
                    R.id.include_main_nav_drawer_topItems,
                    false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    ReportFragment(),
                    getString(R.string.string_reports),
                    R.drawable.ic_sidemenu_reports,
                    R.id.include_main_nav_drawer_topItems,
                    false
                )
            )

            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    SettingsFragment(),
                    getString(R.string.string_settings),
                    R.drawable.ic_sidemenu_settings,
                    R.id.include_main_nav_drawer_bottomItems,
                    false
                )
            )

            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    null,
                    getString(R.string.string_logout),
                    R.drawable.ic_sidemenu_logout,
                    R.id.include_main_nav_drawer_bottomItems,
                    false
                )
            )

            it.create()
        }
    }

    protected fun toggleDrawer(show: Boolean) {
        navDrawer?.setOpen(show)
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
}