package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.presenters.schedule.UnScheduleDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.InfoDialogFragment
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_DETAIL
import com.quickhandslogistics.utils.AppConstant.Companion.NOTES_NOT_AVAILABLE
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.content_unschedule_detail.*

class UnScheduleDetailActivity : BaseActivity(), UnScheduleDetailContract.View {

    private lateinit var liveLoadsAdapter: UnScheduledWorkItemAdapter
    private lateinit var dropsAdapter: UnScheduledWorkItemAdapter
    private lateinit var outBondsAdapter: UnScheduledWorkItemAdapter
    private lateinit var unScheduleDetailPresenter: UnScheduleDetailPresenter

    private var scheduleIdentity = ""
    private var scheduleDetail: ScheduleDetail? = null

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unschedule_detail)
        setupToolbar(getString(R.string.unscheduled_work))

        unScheduleDetailPresenter = UnScheduleDetailPresenter(this, resources)

        intent.extras?.let { bundle ->
            //scheduleIdentity = bundle.getString(ARG_SCHEDULE_IDENTITY, "")
            scheduleDetail = bundle.getParcelable(ARG_SCHEDULE_DETAIL)

            initializeUI()
            scheduleDetail?.let { scheduleDetail ->
                showScheduleData(scheduleDetail)
            }
            //unScheduleDetailPresenter.getScheduleDetail(scheduleIdentity)
        }
    }

    private fun initializeUI() {
        recyclerViewLiveLoad.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            liveLoadsAdapter =
                UnScheduledWorkItemAdapter(resources, getString(R.string.string_live_loads))
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
            outBondsAdapter =
                UnScheduledWorkItemAdapter(resources, getString(R.string.string_out_bounds))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
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
            R.id.actionNotes -> {
                val dialog =
                    InfoDialogFragment.newInstance(scheduleDetail?.scheduleNote!!,
                        showInfoIcon = false,
                        onClickListener = object : InfoDialogContract.View.OnClickListener {
                            override fun onPositiveButtonClick() {
                            }
                        })
                dialog.show(supportFragmentManager, InfoDialogFragment::class.simpleName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*
    * Presenter Listeners
    */
    override fun showScheduleData(scheduleDetail: ScheduleDetail) {
        this.scheduleDetail = scheduleDetail
        invalidateOptionsMenu()

        textViewBuildingName.text = scheduleDetail.buildingName
        scheduleDetail.endDateForCurrentWorkItem?.let {
            textViewScheduleDate.text =
                DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = scheduleDetail.scheduleTypeNames
        textViewWorkItemsCount.text = String.format(
            getString(R.string.work_items_count),
            scheduleDetail.totalNumberOfWorkItems
        )

        scheduleDetail.scheduleTypes?.let { scheduleTypes ->

            // Show LiveLoads work Items Listing
            if (!scheduleTypes.liveLoads.isNullOrEmpty()) {
                scheduleTypes.liveLoads!!.sortWith(Comparator { workItem1, workItem2 ->
                    workItem1.sequence!!.compareTo(workItem2.sequence!!)
                })
                liveLoadsAdapter.updateData(scheduleTypes.liveLoads!!)
                layoutLiveLoadScheduleType.visibility = View.VISIBLE
                recyclerViewLiveLoad.visibility = View.VISIBLE
            } else {
                layoutLiveLoadScheduleType.visibility = View.GONE
                recyclerViewLiveLoad.visibility = View.GONE
            }

            // Show Drops work Items Listing
            if (!scheduleTypes.drops.isNullOrEmpty()) {
                dropsAdapter.updateData(scheduleTypes.drops!!)
                layoutDropsScheduleType.visibility = View.VISIBLE
                recyclerViewDrops.visibility = View.VISIBLE
            } else {
                layoutDropsScheduleType.visibility = View.GONE
                recyclerViewDrops.visibility = View.GONE
            }

            // Show Out Bonds work Items Listing
            if (!scheduleTypes.outbounds.isNullOrEmpty()) {
                scheduleTypes.outbounds!!.sortWith(Comparator { workItem1, workItem2 ->
                    workItem1.sequence!!.compareTo(workItem2.sequence!!)
                })
                outBondsAdapter.updateData(scheduleTypes.outbounds!!)
                layoutOutBondsScheduleType.visibility = View.VISIBLE
                recyclerViewOutBonds.visibility = View.VISIBLE
            } else {
                layoutOutBondsScheduleType.visibility = View.GONE
                recyclerViewOutBonds.visibility = View.GONE
            }
        }
    }

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(activity).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        clearAllData()
        SnackBarFactory.createSnackBar(activity,
            mainConstraintLayout, message, getString(R.string.goBack),
            View.OnClickListener {
                onBackPressed()
            })
    }
}