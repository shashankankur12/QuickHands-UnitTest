package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.presenters.DashBoardPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.view.activities.LeadProfileActivity
import kotlinx.android.synthetic.main.nav_header_dashboard.*

class DashBoardActivity : BaseActivity(), View.OnClickListener, DashBoardContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setupToolbar(showBackButton = false)
        setUpNavigationBar()

        mainHeaderLayout.setOnClickListener(this)

        val dashBoardPresenter = DashBoardPresenter(this, sharedPref)
        dashBoardPresenter.loadLeadProfileData()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                mainHeaderLayout.id -> {
                    toggleDrawer(false)
                    startIntent(LeadProfileActivity::class.java)
                }
            }
        }
    }

    override fun loadLeadProfile(fullName: String, email: String, employeeId: String) {
        textViewLeadName.text = fullName
        textViewEmail.text = email
        textViewEmployeeId.text = String.format("Emp ID: %s", employeeId)
    }
}



