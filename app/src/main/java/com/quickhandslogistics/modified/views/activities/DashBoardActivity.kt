package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.view.activities.LeadProfileActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*

class DashBoardActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mainHeaderLayout: ConstraintLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private var mDrawerTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        setUpUI()

//        if (intent.hasExtra("drawer_tab"))
//            moveToLumper()
    }

    private fun setUpUI() {
        text_version.text = String.format("v %s", BuildConfig.VERSION_NAME)

        val headerView: View = navView.getHeaderView(0)
        navController = findNavController(R.id.nav_host_fragment)

        mainHeaderLayout = headerView.findViewById(R.id.mainHeaderLayout)
        mainHeaderLayout.setOnClickListener(this)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dashboard,
                R.id.nav_schedule,
                R.id.nav_lumper,
                R.id.nav_reports,
                R.id.nav_lumper_sheet,
                R.id.nav_customer_sheet,
                R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                mainHeaderLayout.id -> startIntent(LeadProfileActivity::class.java)
            }
        }
    }

//    fun moveToLumper() {
//        mDrawerTab = intent.getIntExtra("drawer_tab", -1)
//        val bundle = Bundle()
//        bundle.putString("lumper_detail", "lumper")
//
//        when (mDrawerTab) {
//            0 -> navController.navigate(R.id.nav_dashboard)
//            1 -> navController.navigate(R.id.nav_schedule)
//            2 -> navController.navigate(R.id.nav_lumper, bundle)
//            3 -> navController.navigate(R.id.nav_reports)
//            4 -> navController.navigate(R.id.nav_lumper_sheet)
//            5 -> navController.navigate(R.id.nav_customer_sheet)
//            6 -> navController.navigate(R.id.nav_settings)
//        }
//    }
}



