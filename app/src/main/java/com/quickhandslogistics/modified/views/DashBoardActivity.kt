package com.quickhandslogistics.modified.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.controls.NavDrawer
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.presenters.DashBoardPresenter
import com.quickhandslogistics.modified.views.attendance.TimeClockAttendanceFragment
import com.quickhandslogistics.modified.views.customerSheet.CustomerSheetFragment
import com.quickhandslogistics.modified.views.lumperSheet.LumperSheetFragment
import com.quickhandslogistics.modified.views.lumpers.LumpersFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment
import com.quickhandslogistics.modified.views.scheduleTime.ScheduleTimeFragment
import com.quickhandslogistics.modified.views.workSheet.AllWorkScheduleCancelActivity
import com.quickhandslogistics.modified.views.workSheet.WorkSheetFragment
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.UIUtils
import com.quickhandslogistics.view.fragments.ReportFragment
import kotlinx.android.synthetic.main.include_main_nav_drawer.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*

class DashBoardActivity : BaseActivity(), View.OnClickListener, DashBoardContract.View, DashBoardContract.View.OnFragmentInteractionListener {

    private var selectedFragmentTitle: String = ""
    private var scheduleTimeNotes: String = ""
    private var isCancelAllScheduleVisible: Boolean = false

    private lateinit var dashBoardPresenter: DashBoardPresenter

    private var navDrawer: NavDrawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_dashboard)
        setupToolbar(showBackButton = false)
        setUpNavigationBar()

        headerLayout.setOnClickListener(this)

        dashBoardPresenter = DashBoardPresenter(this, sharedPref)
        dashBoardPresenter.loadLeadProfileData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            when (selectedFragmentTitle) {
                getString(R.string.work_sheet) -> {
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
                CustomProgressBar.getInstance().showInfoDialog(getString(R.string.string_note), scheduleTimeNotes, activity)
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
            it.addItem(NavDrawer.ActivityNavDrawerItem(WorkSheetFragment(), getString(R.string.work_sheet), R.drawable.ic_sidemenu_dashboard, R.id.linearLayoutTopItems, true))
            it.addItem(NavDrawer.ActivityNavDrawerItem(ScheduleTimeFragment(), getString(R.string.schedule_lumpers_time), R.drawable.ic_sidemenu_schedule, R.id.linearLayoutTopItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(ScheduleMainFragment(), getString(R.string.string_schedule), R.drawable.ic_sidemenu_lumper_sheet, R.id.linearLayoutTopItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(TimeClockAttendanceFragment(), getString(R.string.time_clock_attendance), R.drawable.ic_sidemenu_schedule, R.id.linearLayoutTopItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(LumpersFragment(), getString(R.string.string_lumpers), R.drawable.ic_sidemenu_lumpers, R.id.linearLayoutTopItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(LumperSheetFragment(), getString(R.string.string_lumper_sheet), R.drawable.ic_sidemenu_lumper_sheet, R.id.linearLayoutTopItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(CustomerSheetFragment(), getString(R.string.string_customer_sheet), R.drawable.ic_sidemenu_customer_sheet, R.id.linearLayoutTopItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(ReportFragment(), getString(R.string.string_reports), R.drawable.ic_sidemenu_reports, R.id.linearLayoutTopItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(SettingsFragment(), getString(R.string.string_settings), R.drawable.ic_sidemenu_settings, R.id.linearLayoutBottomItems, false))
            it.addItem(NavDrawer.ActivityNavDrawerItem(null, getString(R.string.string_logout), R.drawable.ic_sidemenu_logout, R.id.linearLayoutBottomItems, false))

            it.create()
        }
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
    override fun showLeadProfile(leadProfileData: LeadProfileData) {
        UIUtils.showEmployeeProfileImage(activity, leadProfileData, circleImageViewProfile)

        textViewLeadName.text = UIUtils.getEmployeeFullName(leadProfileData)
        textViewEmail.text = if (!StringUtils.isNullOrEmpty(leadProfileData.email)) leadProfileData.email else "-"
        textViewEmployeeId.text = if (!StringUtils.isNullOrEmpty(leadProfileData.employeeId)) leadProfileData.employeeId else "-"

        textViewVersionName.text = String.format("v%s", BuildConfig.VERSION_NAME)
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
}