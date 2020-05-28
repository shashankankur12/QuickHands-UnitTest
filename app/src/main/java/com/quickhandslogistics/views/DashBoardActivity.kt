package com.quickhandslogistics.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.controls.NavDrawer
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.presenters.DashBoardPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.attendance.TimeClockAttendanceFragment
import com.quickhandslogistics.views.customerSheet.CustomerSheetFragment
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment
import com.quickhandslogistics.views.lumpers.LumpersFragment
import com.quickhandslogistics.views.reports.ReportFragment
import com.quickhandslogistics.views.schedule.ScheduleFragment
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment
import com.quickhandslogistics.views.workSheet.AllWorkScheduleCancelActivity
import com.quickhandslogistics.views.workSheet.WorkSheetFragment
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.include_main_nav_drawer.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*

class DashBoardActivity : BaseActivity(), View.OnClickListener, DashBoardContract.View, DashBoardContract.View.OnFragmentInteractionListener {

    private var showTabName = ""
    private var scheduleTimeSelectedDate = ""
    private var selectedFragmentTitle: String = ""
    private var scheduleTimeNotes: String = ""
    private var isCancelAllScheduleVisible: Boolean = false

    private lateinit var dashBoardPresenter: DashBoardPresenter

    private var navDrawer: NavDrawer? = null

    companion object {
        const val ARG_SHOW_TAB_NAME = "ARG_SHOW_TAB_NAME"
        const val ARG_SCHEDULE_TIME_SELECTED_DATE = "ARG_SCHEDULE_TIME_SELECTED_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_dashboard)
        setupToolbar(showBackButton = false)

        intent.extras?.also { bundle ->
            showTabName = bundle.getString(ARG_SHOW_TAB_NAME, getString(R.string.today_s_work_sheet))

            //if Tab is Schedule Lumper Time, then check for date
            if (showTabName == getString(R.string.schedule_lumpers_time)) {
                scheduleTimeSelectedDate = bundle.getString(ARG_SCHEDULE_TIME_SELECTED_DATE, "")
            }
        } ?: run {
            showTabName = getString(R.string.today_s_work_sheet)
        }

        setUpNavigationBar()
        headerLayout.setOnClickListener(this)

        dashBoardPresenter = DashBoardPresenter(this, resources, sharedPref)
        dashBoardPresenter.loadLeadProfileData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            when (selectedFragmentTitle) {
                getString(R.string.today_s_work_sheet) -> {
                    menu.findItem(R.id.actionCancelAllWork).isVisible = isCancelAllScheduleVisible
                }
                getString(R.string.schedule_lumpers_time) -> {
                    menu.findItem(R.id.actionNotes).isVisible = scheduleTimeNotes.isNotEmpty()
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionNotes -> {
                CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), scheduleTimeNotes, activity)
            }
            R.id.actionCancelAllWork -> {
                startIntent(AllWorkScheduleCancelActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dashBoardPresenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            navDrawer?.updateWorkSheetList()
        }
    }

    private fun setUpNavigationBar() {
        navDrawer = NavDrawer(this, toolbar, supportFragmentManager.beginTransaction(), this)
        navDrawer?.let {
            it.addItem(
                NavDrawer.AppNavDrawerItem(
                    WorkSheetFragment(), R.drawable.ic_sidemenu_dashboard, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.today_s_work_sheet), showTabName)
                )
            )

            val scheduleTimeFragment = ScheduleTimeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SCHEDULE_TIME_SELECTED_DATE, scheduleTimeSelectedDate)
                }
            }
            it.addItem(
                NavDrawer.AppNavDrawerItem(
                    scheduleTimeFragment, R.drawable.ic_sidemenu_schedule, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.schedule_lumpers_time), showTabName)
                )
            )

            it.addItem(NavDrawer.AppNavDrawerItem(ScheduleFragment(), R.drawable.ic_sidemenu_lumper_sheet, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.schedule), showTabName)))
            it.addItem(
                NavDrawer.AppNavDrawerItem(
                    TimeClockAttendanceFragment(), R.drawable.ic_sidemenu_schedule, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.time_clock_attendance), showTabName)
                )
            )
            it.addItem(NavDrawer.AppNavDrawerItem(LumpersFragment(), R.drawable.ic_sidemenu_lumpers, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.lumpers), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(LumperSheetFragment(), R.drawable.ic_sidemenu_lumper_sheet, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.l_sheet), showTabName)))
            it.addItem(
                NavDrawer.AppNavDrawerItem(
                    CustomerSheetFragment(), R.drawable.ic_sidemenu_customer_sheet, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.customer_sheet), showTabName)
                )
            )
            it.addItem(NavDrawer.AppNavDrawerItem(ReportFragment(), R.drawable.ic_sidemenu_reports, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.reports), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(SettingsFragment(), R.drawable.ic_sidemenu_settings, R.id.linearLayoutBottomItems, isShowOnLaunch(getString(R.string.settings), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(null, R.drawable.ic_sidemenu_logout, R.id.linearLayoutBottomItems, isShowOnLaunch(getString(R.string.logout), showTabName)))

            it.create()
        }
    }

    private fun isShowOnLaunch(tabName: String, showTabName: String): Pair<String, Boolean> {
        val isShowOnLaunch = showTabName == tabName
        return Pair(tabName, isShowOnLaunch)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                headerLayout.id -> {
                    navDrawer?.setOpen(false)
                    startIntent(LeadProfileActivity::class.java)
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, frameLayoutMain, message)
    }

    override fun showLeadProfile(leadProfileData: LeadProfileData) {
        UIUtils.showEmployeeProfileImage(activity, leadProfileData, circleImageViewProfile)

        textViewLeadName.text = UIUtils.getEmployeeFullName(leadProfileData)
        textViewEmail.text = if (!leadProfileData.email.isNullOrEmpty()) leadProfileData.email else "-"
        textViewEmployeeId.text = if (!leadProfileData.employeeId.isNullOrEmpty()) leadProfileData.employeeId else "-"

        textViewVersionName.text = String.format("v%s", BuildConfig.VERSION_NAME)
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Child Fragment Interaction Listeners */
    override fun onNewFragmentReplaced(title: String) {
        selectedFragmentTitle = title
        invalidateOptionsMenu()
    }

    override fun invalidateScheduleTimeNotes(notes: String) {
        this.scheduleTimeNotes = notes
        invalidateOptionsMenu()
    }

    override fun invalidateCancelAllSchedulesOption(isShown: Boolean) {
        this.isCancelAllScheduleVisible = isShown
        invalidateOptionsMenu()
    }

    override fun onLogoutOptionSelected() {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.logout_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                dashBoardPresenter.performLogout()
            }

            override fun onCancelClick() {
            }
        })
    }
}