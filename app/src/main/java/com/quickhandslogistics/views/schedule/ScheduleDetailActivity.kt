package com.quickhandslogistics.views.schedule

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.adapters.schedule.ScheduledWorkItemAdapter
import com.quickhandslogistics.contracts.common.LumperImagesContract
import com.quickhandslogistics.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.controls.OverlapDecoration
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.schedule.ScheduleDetailPresenter
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.NOTES_NOT_AVAILABLE
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.common.DisplayLumpersListActivity
import com.quickhandslogistics.views.common.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_IS_FUTURE_DATE
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULE_IDENTITY
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import kotlinx.android.synthetic.main.content_schedule_detail.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class ScheduleDetailActivity : BaseActivity(), LumperImagesContract.OnItemClickListener,
    ScheduleDetailContract.View, ScheduleDetailContract.View.OnAdapterItemClickListener {

    private var allowUpdate: Boolean = false
    private var isFutureDate: Boolean = false
    private var selectedTime: Long = 0
    private var scheduleIdentity = ""
    private var scheduleDetail: ScheduleDetail? = null

    private lateinit var allLumpersImagesAdapter: LumperImagesAdapter
    private lateinit var liveLoadsAdapter: ScheduledWorkItemAdapter
    private lateinit var dropsAdapter: ScheduledWorkItemAdapter
    private lateinit var outBondsAdapter: ScheduledWorkItemAdapter
    private lateinit var scheduleDetailPresenter: ScheduleDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        setupToolbar(getString(R.string.schedule_detail))


        intent.extras?.let { bundle ->
            allowUpdate = bundle.getBoolean(ARG_ALLOW_UPDATE)
            isFutureDate = bundle.getBoolean(ARG_IS_FUTURE_DATE)
            scheduleIdentity = bundle.getString(ARG_SCHEDULE_IDENTITY, "")
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
        }

        initializeUI()

        scheduleDetailPresenter = ScheduleDetailPresenter(this, resources)
        savedInstanceState?.also {
            if (savedInstanceState.containsKey("scheduleDetail")) {
                scheduleDetail = savedInstanceState.getParcelable("scheduleDetail")
                showScheduleData(scheduleDetail!!)
            }
        } ?: run {
            scheduleDetailPresenter.getScheduleDetail(scheduleIdentity, Date(selectedTime))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if(scheduleDetail!=null)
        outState.putParcelable("scheduleDetail", scheduleDetail)
        super.onSaveInstanceState(outState)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val menuItem = menu.findItem(R.id.actionNotes)
            menuItem.isVisible = !scheduleDetail?.scheduleNote.isNullOrEmpty() &&
                    scheduleDetail?.scheduleNote != NOTES_NOT_AVAILABLE
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionNotes -> CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), scheduleDetail?.scheduleNote!!, activity)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            scheduleDetailPresenter.getScheduleDetail(scheduleIdentity, Date(selectedTime))
            setResult(RESULT_OK)
        }
    }

    private fun initializeUI() {
        recyclerViewLiveLoad.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            liveLoadsAdapter = ScheduledWorkItemAdapter(resources, getString(R.string.live_loads), isFutureDate, this@ScheduleDetailActivity)
            adapter = liveLoadsAdapter
        }

        recyclerViewDrops.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            dropsAdapter = ScheduledWorkItemAdapter(resources, getString(R.string.drops), isFutureDate, this@ScheduleDetailActivity)
            adapter = dropsAdapter
        }

        recyclerViewOutBonds.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            outBondsAdapter = ScheduledWorkItemAdapter(resources, getString(R.string.out_bounds), isFutureDate, this@ScheduleDetailActivity)
            adapter = outBondsAdapter
        }

        recyclerViewLumpersImagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(OverlapDecoration())
            allLumpersImagesAdapter = LumperImagesAdapter(ArrayList(), this@ScheduleDetailActivity)
            adapter = allLumpersImagesAdapter
        }
    }

    private fun showHeaderInfo(scheduleDetail: ScheduleDetail) {
        if (!scheduleDetail.buildingName.isNullOrEmpty())
            textViewBuildingName.text = scheduleDetail.buildingName?.capitalize()

        scheduleDetail.endDateForCurrentWorkItem?.let {
            textViewScheduleDate.text = DateUtils.changeDateString(PATTERN_API_RESPONSE, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = scheduleDetail.scheduleTypeNames
        textViewWorkItemsCount.text = String.format(getString(R.string.work_items_s), scheduleDetail.totalNumberOfWorkItems)
    }

    private fun showLiveLoadsList(scheduleTypes: ScheduleDetail.ScheduleTypes): ArrayList<EmployeeData> {
        val allLumpersList = ArrayList<EmployeeData>()
        if (!scheduleTypes.liveLoads.isNullOrEmpty()) {
            scheduleTypes.liveLoads!!.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            liveLoadsAdapter.updateData(scheduleTypes.liveLoads!!)
            layoutLiveLoadScheduleType.visibility = View.VISIBLE
            recyclerViewLiveLoad.visibility = View.VISIBLE

            for (workItem in scheduleTypes.liveLoads!!) {
                allLumpersList.addAll(workItem.assignedLumpersList!!)
            }
        } else {
            layoutLiveLoadScheduleType.visibility = View.GONE
            recyclerViewLiveLoad.visibility = View.GONE
        }
        return allLumpersList
    }

    private fun showDropsList(scheduleTypes: ScheduleDetail.ScheduleTypes): ArrayList<EmployeeData> {
        val allLumpersList = ArrayList<EmployeeData>()
        if (!scheduleTypes.drops.isNullOrEmpty()) {
            dropsAdapter.updateData(scheduleTypes.drops!!)
            layoutDropsScheduleType.visibility = View.VISIBLE
            recyclerViewDrops.visibility = View.VISIBLE

            for (workItem in scheduleTypes.drops!!) {
                allLumpersList.addAll(workItem.assignedLumpersList!!)
            }
        } else {
            layoutDropsScheduleType.visibility = View.GONE
            recyclerViewDrops.visibility = View.GONE
        }
        return allLumpersList
    }

    private fun showOutBoundsList(scheduleTypes: ScheduleDetail.ScheduleTypes): ArrayList<EmployeeData> {
        val allLumpersList = ArrayList<EmployeeData>()
        if (!scheduleTypes.outbounds.isNullOrEmpty()) {
            scheduleTypes.outbounds!!.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            outBondsAdapter.updateData(scheduleTypes.outbounds!!)
            layoutOutBondsScheduleType.visibility = View.VISIBLE
            recyclerViewOutBonds.visibility = View.VISIBLE

            for (workItem in scheduleTypes.outbounds!!) {
                allLumpersList.addAll(workItem.assignedLumpersList!!)
            }
        } else {
            layoutOutBondsScheduleType.visibility = View.GONE
            recyclerViewOutBonds.visibility = View.GONE
        }
        return allLumpersList
    }


    /** Presenter Listeners */
    override fun showScheduleData(scheduleDetail: ScheduleDetail) {
        this.scheduleDetail = scheduleDetail
        invalidateOptionsMenu()

        showHeaderInfo(scheduleDetail)

        var allLumpersList = ArrayList<EmployeeData>()

        scheduleDetail.scheduleTypes?.let { scheduleTypes ->
            // Show Live Loads work Items Listing
            allLumpersList.addAll(showLiveLoadsList(scheduleTypes))

            // Show Drops work Items Listing
            allLumpersList.addAll(showDropsList(scheduleTypes))

            // Show Out Bonds work Items Listing
            allLumpersList.addAll(showOutBoundsList(scheduleTypes))
        }

        if (allLumpersList.size > 0) {
            allLumpersList = allLumpersList.distinctBy { it.id } as ArrayList<EmployeeData>
            allLumpersImagesAdapter.updateData(allLumpersList)
            recyclerViewLumpersImagesList.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersImagesList.visibility = View.GONE
        }
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    /** Adapter Listeners */
    override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    override fun onWorkItemClick(workItemDetail: WorkItemDetail, workItemTypeDisplayName: String) {
        var canUpdate = true
        //Check is work item is cancelled or completed. If yes, then make is non-editable
        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                canUpdate = false
            }
        }
        val bundle = Bundle()
        bundle.putBoolean(ARG_ALLOW_UPDATE, (allowUpdate && canUpdate))
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
        bundle.putString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, workItemTypeDisplayName)
        startIntent(ScheduledWorkItemDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }
}