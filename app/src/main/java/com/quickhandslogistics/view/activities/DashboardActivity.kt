package com.quickhandslogistics.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.layout_header.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController : NavController
    private var mDrawerTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        navController = findNavController(R.id.nav_host_fragment)

        if (intent.hasExtra("drawer_tab"))
            moveToLumper()
        /* val relativeLayout = findViewById<RelativeLayout>(R.id.relative_root)

        relativeLayout.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        })*/

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
        nav_view.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(menu: MenuItem): Boolean {
        when (menu.itemId) {
            R.id.nav_logout -> {
                //   navController.navigate(R.id.)
            }
        }
        return false
    }

    fun moveToLumper() {
        mDrawerTab = intent.getIntExtra("drawer_tab", -1)
        var bundle = bundleOf("lumper_detail" to "lum")

        when (mDrawerTab) {

            0 -> navController?.navigate(R.id.nav_dashboard)
            1 -> navController?.navigate(R.id.nav_schedule)
            2 ->  navController?.navigate(R.id.nav_lumper,bundle)
            3 -> navController?.navigate(R.id.nav_reports)
            4 -> navController?.navigate(R.id.nav_lumper_sheet)
            5 -> navController?.navigate(R.id.nav_customer_sheet)
            6 -> navController?.navigate(R.id.nav_settings)
        }
    }
}

