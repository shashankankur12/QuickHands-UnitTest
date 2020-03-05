package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.SharedMemory
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USERFIRSTNAME
import com.quickhandslogistics.utils.AppConstant.Companion.PREF_USER_NAME
import com.quickhandslogistics.utils.SharedPref
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*
import org.w3c.dom.Text

class DashboardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController : NavController
    private var mDrawerTab = 0
    private var leadName  : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        text_version.text = "v " + BuildConfig.VERSION_NAME

        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        var drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        
        var navView: NavigationView = findViewById(R.id.nav_view)
        var view:View = navView.getHeaderView(0)

        navController = findNavController(R.id.nav_host_fragment)

        if (intent.hasExtra("drawer_tab"))
            moveToLumper()
        val relativeLayout = view.findViewById<ConstraintLayout>(R.id.relative_root)

        relativeLayout.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LeadProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        })

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

    fun moveToLumper() {
        mDrawerTab = intent.getIntExtra("drawer_tab", -1)
        val bundle = Bundle()
        bundle.putString("lumper_detail", "lumper")

        when (mDrawerTab) {

            0 -> navController?.navigate(R.id.nav_dashboard)
            1 -> navController?.navigate(R.id.nav_schedule)
            2 -> navController?.navigate(R.id.nav_lumper, bundle)
            3 -> navController?.navigate(R.id.nav_reports)
            4 -> navController?.navigate(R.id.nav_lumper_sheet)
            5 -> navController?.navigate(R.id.nav_customer_sheet)
            6 -> navController?.navigate(R.id.nav_settings)
        }
    }

}



