package com.quickhandslogistics.modified.views.schedule

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.schedule.UnScheduledWorkItemAdapter
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.presenters.schedule.UnScheduleDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_DETAIL
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_FROM_DATE
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_IDENTITY
import com.quickhandslogistics.utils.AppConstant.Companion.NOTES_NOT_AVAILABLE
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.content_schedule_detail.*

class UnScheduleDetailActivity : BaseActivity(), UnScheduleDetailContract.View {

    private var scheduleDetail: ScheduleDetail? = null

    private lateinit var liveLoadsAdapter: UnScheduledWorkItemAdapter
    private lateinit var dropsAdapter: UnScheduledWorkItemAdapter
    private lateinit var outBondsAdapter: UnScheduledWorkItemAdapter
    private lateinit var unScheduleDetailPresenter: UnScheduleDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        setupToolbar(getString(R.string.unscheduled_work))

        initializeUI()

        unScheduleDetailPresenter = UnScheduleDetailPresenter(this, resources)

        intent.extras?.let { bundle ->
            when {
                bundle.containsKey(ARG_SCHEDULE_DETAIL) -> {
                    // Show Schedule Detail passed from previous listing
                    scheduleDetail = bundle.getParcelable(ARG_SCHEDULE_DETAIL)
                    scheduleDetail?.let { scheduleDetail ->
                        showScheduleData(scheduleDetail, scheduleDetail.endDateForCurrentWorkItem)
                    }
                }
                bundle.containsKey(ARG_SCHEDULE_IDENTITY) -> {
                    // Fetch Schedule Detail using id & date
                    val scheduleIdentity = bundle.getString(ARG_SCHEDULE_IDENTITY, "")
                    val scheduleFromDate = bundle.getString(ARG_SCHEDULE_FROM_DATE, "")
                    unScheduleDetailPresenter.getScheduleDetail(scheduleIdentity, scheduleFromDate)
                }
                else -> {
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val menuItem = menu.findItem(R.id.actionNotes)
            menuItem.isVisible = !scheduleDetail?.scheduleNote.isNullOrEmpty() && scheduleDetail?.scheduleNote != NOTES_NOT_AVAILABLE
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionNotes -> CustomProgressBar.getInstance().showInfoDialog(getString(R.string.string_note), scheduleDetail?.scheduleNote!!, activity)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeUI() {
        recyclerViewLiveLoad.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            liveLoadsAdapter = UnScheduledWorkItemAdapter(resources, getString(R.string.string_live_loads))
            adapter = liveLoadsAdapter
        }

        recyclerViewDrops.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            dropsAdapter = UnScheduledWorkItemAdapter(resources, getString(R.string.string_drops))
            adapter = dropsAdapter
        }

        recyclerViewOutBonds.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            outBondsAdapter = UnScheduledWorkItemAdapter(resources, getString(R.string.string_out_bounds))
            adapter = outBondsAdapter
        }
    }

    private fun clearAllData() {
        textViewBuildingName.visibility = View.INVISIBLE
        textViewScheduleDate.visibility = View.INVISIBLE
        textViewScheduleType.visibility = View.INVISIBLE
        textViewWorkItemsCount.visibility = View.INVISIBLE
        layoutAllWorkItems.visibility = View.GONE
    }

    private fun showLiveLoadsList(scheduleTypes: ScheduleDetail.ScheduleTypes) {
        // Show LiveLoads work Items Listing
        if (!scheduleTypes.liveLoads.isNullOrEmpty()) {
            scheduleTypes.liveLoads!!.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            liveLoadsAdapter.updateData(scheduleTypes.liveLoads!!)
            layoutLiveLoadScheduleType.visibility = View.VISIBLE
            recyclerViewLiveLoad.visibility = View.VISIBLE
        } else {
            layoutLiveLoadScheduleType.visibility = View.GONE
            recyclerViewLiveLoad.visibility = View.GONE
        }
    }

    private fun showDropsList(scheduleTypes: ScheduleDetail.ScheduleTypes) {
        // Show Drops work Items Listing
        if (!scheduleTypes.drops.isNullOrEmpty()) {
            dropsAdapter.updateData(scheduleTypes.drops!!)
            layoutDropsScheduleType.visibility = View.VISIBLE
            recyclerViewDrops.visibility = View.VISIBLE
        } else {
            layoutDropsScheduleType.visibility = View.GONE
            recyclerViewDrops.visibility = View.GONE
        }
    }

    private fun showOutBoundsList(scheduleTypes: ScheduleDetail.ScheduleTypes) {
        // Show Out Bonds work Items Listing
        if (!scheduleTypes.outbounds.isNullOrEmpty()) {
            scheduleTypes.outbounds!!.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            outBondsAdapter.updateData(scheduleTypes.outbounds!!)
            layoutOutBondsScheduleType.visibility = View.VISIBLE
            recyclerViewOutBonds.visibility = View.VISIBLE
        } else {
            layoutOutBondsScheduleType.visibility = View.GONE
            recyclerViewOutBonds.visibility = View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showScheduleData(scheduleDetail: ScheduleDetail, scheduleDate: String?) {
        this.scheduleDetail = scheduleDetail
        invalidateOptionsMenu()

        if (!scheduleDetail.buildingName.isNullOrEmpty())
            textViewBuildingName.text = scheduleDetail.buildingName?.capitalize()

        scheduleDate?.let {
            textViewScheduleDate.text = DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = scheduleDetail.scheduleTypeNames
        textViewWorkItemsCount.text = String.format(getString(R.string.work_items_count), scheduleDetail.totalNumberOfWorkItems)

        scheduleDetail.scheduleTypes?.let { scheduleTypes ->
            showLiveLoadsList(scheduleTypes)
            showDropsList(scheduleTypes)
            showOutBoundsList(scheduleTypes)
        }
    }

    override fun showAPIErrorMessage(message: String) {
        clearAllData()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message, getString(R.string.goBack), View.OnClickListener { onBackPressed() })
    }
}