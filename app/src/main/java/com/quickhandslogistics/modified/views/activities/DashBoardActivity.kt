package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.View
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.Dashboard.DashboardLeadProfileData
import com.quickhandslogistics.modified.presenters.DashBoardPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.include_main_nav_drawer.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*

class DashBoardActivity : BaseActivity(), View.OnClickListener, DashBoardContract.View {
    private lateinit var dashBoardPresenter: DashBoardPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setupToolbar(showBackButton = false)
        setUpNavigationBar()

        headerLayout.setOnClickListener(this)

        dashBoardPresenter = DashBoardPresenter(this,sharedPref)
        dashBoardPresenter.loadLeadProfileData()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                headerLayout.id -> {
                    toggleDrawer(false)
                    startIntent(LeadProfileActivity::class.java)
                }
            }
        }
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, drawerLayout, message)
    }

    override fun showLumpersData(employeeDataListDashboard: DashboardLeadProfileData) {
        if (!StringUtils.isNullOrEmpty(employeeDataListDashboard.profileImageUrl))
            Picasso.get().load(employeeDataListDashboard.profileImageUrl).placeholder(R.drawable.dummy)
                .error(R.drawable.dummy)
                .into(circleImageViewProfile)

        textViewLeadName.text = String.format(
            "%s %s",
            ValueUtils.getDefaultOrValue(employeeDataListDashboard.firstName),
            ValueUtils.getDefaultOrValue(employeeDataListDashboard.lastName)
        )

        textViewEmail.text =
            if (!StringUtils.isNullOrEmpty(employeeDataListDashboard.email)) employeeDataListDashboard.email else "-"

        textViewEmployeeId.text =
            if (!StringUtils.isNullOrEmpty(employeeDataListDashboard.employeeId)) employeeDataListDashboard.employeeId else "-"

        textViewVersionName.text = String.format("V %s", BuildConfig.VERSION_NAME)
    }
}



