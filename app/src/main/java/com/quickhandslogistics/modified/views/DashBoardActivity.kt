package com.quickhandslogistics.modified.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.contracts.common.InfoDialogContract
import com.quickhandslogistics.modified.controls.NavDrawer
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.presenters.DashBoardPresenter
import com.quickhandslogistics.modified.views.attendance.TimeClockAttendanceFragment
import com.quickhandslogistics.modified.views.common.InfoDialogFragment
import com.quickhandslogistics.modified.views.lumperSheet.LumperSheetFragment
import com.quickhandslogistics.modified.views.lumpers.LumpersFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment
import com.quickhandslogistics.modified.views.scheduleTime.ScheduleTimeFragment
import com.quickhandslogistics.modified.views.workSheet.AllWorkScheduleCancelActivity
import com.quickhandslogistics.modified.views.workSheet.WorkSheetFragment
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import com.quickhandslogistics.view.fragments.CustomerSheetFragment
import com.quickhandslogistics.view.fragments.ReportFragment
import kotlinx.android.synthetic.main.include_main_nav_drawer.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*

class DashBoardActivity : BaseActivity(), View.OnClickListener, DashBoardContract.View,
    DashBoardContract.View.OnFragmentInteractionListener {

    private var selectedFragmentTitle: String = ""
    private var scheduleTimeNotes: String = ""
    private var isCancelAllScheduleVisible: Boolean = false

    private lateinit var dashBoardPresenter: DashBoardPresenter

    private var navDrawer: NavDrawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val dialog = InfoDialogFragment.newInstance(scheduleTimeNotes,
                    showInfoIcon = false,
                    onClickListener = object : InfoDialogContract.View.OnClickListener {
                        override fun onPositiveButtonClick() {
                        }
                    })
                dialog.show(supportFragmentManager, InfoDialogFragment::class.simpleName)
            }
            R.id.actionCancelAllWork -> {
                startIntent(
                    AllWorkScheduleCancelActivity::class.java,
                    requestCode = AppConstant.REQUEST_CODE_CHANGED
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        dashBoardPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            navDrawer?.updateWorkSheetList()
        }
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

    private fun setUpNavigationBar() {
        navDrawer = NavDrawer(this, toolbar, supportFragmentManager.beginTransaction(), this)
        navDrawer?.let {
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    WorkSheetFragment(), getString(R.string.work_sheet),
                    R.drawable.ic_sidemenu_dashboard,
                    R.id.include_main_nav_drawer_topItems, true
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    ScheduleTimeFragment(), getString(R.string.schedule_lumpers_time),
                    R.drawable.ic_sidemenu_schedule,
                    R.id.include_main_nav_drawer_topItems, false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    ScheduleMainFragment(), getString(R.string.string_schedule),
                    R.drawable.ic_sidemenu_lumper_sheet,
                    R.id.include_main_nav_drawer_topItems, false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    TimeClockAttendanceFragment(), getString(R.string.time_clock_attendance),
                    R.drawable.ic_sidemenu_schedule,
                    R.id.include_main_nav_drawer_topItems, false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    LumpersFragment(), getString(R.string.string_lumpers),
                    R.drawable.ic_sidemenu_lumpers,
                    R.id.include_main_nav_drawer_topItems, false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    LumperSheetFragment(), getString(R.string.string_lumper_sheet),
                    R.drawable.ic_sidemenu_lumper_sheet,
                    R.id.include_main_nav_drawer_topItems, false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    CustomerSheetFragment(), getString(R.string.string_customer_sheet),
                    R.drawable.ic_sidemenu_customer_sheet,
                    R.id.include_main_nav_drawer_topItems, false
                )
            )
            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    ReportFragment(), getString(R.string.string_reports),
                    R.drawable.ic_sidemenu_reports,
                    R.id.include_main_nav_drawer_topItems, false
                )
            )

            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    SettingsFragment(), getString(R.string.string_settings),
                    R.drawable.ic_sidemenu_settings,
                    R.id.include_main_nav_drawer_bottomItems, false
                )
            )

            it.addItem(
                NavDrawer.ActivityNavDrawerItem(
                    null, getString(R.string.string_logout), R.drawable.ic_sidemenu_logout,
                    R.id.include_main_nav_drawer_bottomItems, false
                )
            )

            it.create()
        }
    }

    private fun toggleDrawer(show: Boolean) {
        navDrawer?.setOpen(show)
    }

    override fun showLeadProfile(leadProfileData: LeadProfileData) {
        if (!StringUtils.isNullOrEmpty(leadProfileData.profileImageUrl)) {
            Glide.with(activity).load(leadProfileData.profileImageUrl)
                .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                .into(circleImageViewProfile)
        } else {
            Glide.with(activity).clear(circleImageViewProfile);
        }

        textViewLeadName.text = String.format(
            "%s %s",
            ValueUtils.getDefaultOrValue(leadProfileData.firstName),
            ValueUtils.getDefaultOrValue(leadProfileData.lastName)
        )

        textViewEmail.text =
            if (!StringUtils.isNullOrEmpty(leadProfileData.email)) leadProfileData.email else "-"

        textViewEmployeeId.text =
            if (!StringUtils.isNullOrEmpty(leadProfileData.employeeId)) leadProfileData.employeeId else "-"

        textViewVersionName.text = String.format("V %s", BuildConfig.VERSION_NAME)
    }

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