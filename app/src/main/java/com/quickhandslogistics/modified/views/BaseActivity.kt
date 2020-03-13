package com.quickhandslogistics.modified.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.controls.NavDrawer
import com.quickhandslogistics.modified.views.fragments.lumperSheet.LumperSheetFragment
import com.quickhandslogistics.modified.views.fragments.lumpers.LumpersFragment
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.view.fragments.*
import kotlinx.android.synthetic.main.layout_toolbar.*

open class BaseActivity : AppCompatActivity() {

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    protected lateinit var sharedPref: SharedPref

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
                    LogoutDialog(),
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
}