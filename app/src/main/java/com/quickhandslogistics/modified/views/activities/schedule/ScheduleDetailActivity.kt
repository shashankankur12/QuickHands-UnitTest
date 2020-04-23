package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogContract
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.ScheduleDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.schedule.ScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.InfoDialogFragment
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_IDENTITY
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.NOTES_NOT_AVAILABLE
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.content_schedule_detail.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class ScheduleDetailActivity : BaseActivity(), LumperImagesContract.OnItemClickListener,
    ScheduleDetailContract.View, ScheduleDetailContract.View.OnAdapterItemClickListener {

    private lateinit var allLumpersImagesAdapter: LumperImagesAdapter
    private lateinit var liveLoadsAdapter: ScheduledWorkItemAdapter
    private lateinit var dropsAdapter: ScheduledWorkItemAdapter
    private lateinit var outBondsAdapter: ScheduledWorkItemAdapter
    private lateinit var scheduleDetailPresenter: ScheduleDetailPresenter

    private var allowUpdate: Boolean = false
    private var selectedTime: Long = 0
    private var scheduleIdentity = ""
    private var scheduleDetail: ScheduleDetail? = null

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        setupToolbar(getString(R.string.scheduled_work))

        scheduleDetailPresenter = ScheduleDetailPresenter(this, resources)

        intent.extras?.let { bundle ->
            allowUpdate = bundle.getBoolean(ARG_ALLOW_UPDATE)
            scheduleIdentity = bundle.getString(ARG_SCHEDULE_IDENTITY, "")
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)

            initializeUI()
            scheduleDetailPresenter.getScheduleDetail(scheduleIdentity, Date(selectedTime))
        }
    }

    private fun initializeUI() {
        recyclerViewLiveLoad.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            liveLoadsAdapter = ScheduledWorkItemAdapter(
                resources, getString(R.string.string_live_loads),
                adapterItemClickListener = this@ScheduleDetailActivity
            )
            adapter = liveLoadsAdapter
        }

        recyclerViewDrops.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            dropsAdapter = ScheduledWorkItemAdapter(
                resources, getString(R.string.string_drops),
                adapterItemClickListener = this@ScheduleDetailActivity
            )
            adapter = dropsAdapter
        }

        recyclerViewOutBonds.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            outBondsAdapter = ScheduledWorkItemAdapter(
                resources, getString(R.string.string_out_bounds),
                adapterItemClickListener = this@ScheduleDetailActivity
            )
            adapter = outBondsAdapter
        }

        recyclerViewLumpersImagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(OverlapDecoration())
            allLumpersImagesAdapter =
                LumperImagesAdapter(ArrayList(), this@ScheduleDetailActivity)
            adapter = allLumpersImagesAdapter
        }
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
                val dialog = InfoDialogFragment.newInstance(scheduleDetail?.scheduleNote!!,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            scheduleDetailPresenter.getScheduleDetail(scheduleIdentity, Date(selectedTime))
            setResult(RESULT_OK)
        }
    }

    /*
    * Presenter Listeners
    */
    override fun showScheduleData(scheduleDetail: ScheduleDetail) {
        this.scheduleDetail = scheduleDetail
        invalidateOptionsMenu()

        textViewBuildingName.text = scheduleDetail.buildingName
        scheduleDetail.scheduledFrom?.let {
            textViewScheduleDate.text =
                DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = scheduleDetail.scheduleTypeNames
        textViewWorkItemsCount.text = String.format(
            getString(R.string.work_items_count),
            scheduleDetail.totalNumberOfWorkItems
        )

        var allLumpersList = ArrayList<EmployeeData>()

        scheduleDetail.scheduleTypes?.let { scheduleTypes ->
            // Show Live Loads work Items Listing
            if (!scheduleTypes.liveLoads.isNullOrEmpty()) {
                scheduleTypes.liveLoads!!.sortWith(Comparator { workItem1, workItem2 ->
                    workItem1.sequence!!.compareTo(workItem2.sequence!!)
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

            // Show Drops work Items Listing
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

            // Show Out Bonds work Items Listing
            if (!scheduleTypes.outbounds.isNullOrEmpty()) {
                scheduleTypes.outbounds!!.sortWith(Comparator { workItem1, workItem2 ->
                    workItem1.sequence!!.compareTo(workItem2.sequence!!)
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
        }

        if (allLumpersList.size > 0) {
            allLumpersList = allLumpersList.distinctBy { it.id } as ArrayList<EmployeeData>
            allLumpersImagesAdapter.updateData(allLumpersList)
            recyclerViewLumpersImagesList.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersImagesList.visibility = View.GONE
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
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    /*
    * Adapter Item Click Listeners
    */
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

    override fun onWorkItemClick(
        workItemDetail: WorkItemDetail,
        workItemTypeDisplayName: String
    ) {
        val bundle = Bundle()
        bundle.putBoolean(ARG_ALLOW_UPDATE, allowUpdate)
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
        bundle.putString(ARG_WORK_ITEM_TYPE, workItemDetail.workItemType)
        bundle.putString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, workItemTypeDisplayName)
        startIntent(
            ScheduledWorkItemDetailActivity::class.java, bundle = bundle,
            requestCode = AppConstant.REQUEST_CODE_CHANGED
        )
    }
}