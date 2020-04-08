package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.UnScheduleDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_IS_SCHEDULED_STATUS_CHANGED
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.content_unschedule_detail.*

class UnScheduleDetailActivity : BaseActivity(), LumperImagesContract.OnItemClickListener,
    UnScheduleDetailContract.View, UnScheduleDetailContract.View.OnAdapterItemClickListener {

    private lateinit var allLumpersImagesAdapter: LumperImagesAdapter
    private lateinit var liveLoadsAdapter: UnScheduledWorkItemAdapter
    private lateinit var dropsAdapter: UnScheduledWorkItemAdapter
    private lateinit var outBondsAdapter: UnScheduledWorkItemAdapter
    private lateinit var unScheduleDetailPresenter: UnScheduleDetailPresenter

    private var scheduleDetail: ScheduleDetail? = null

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unschedule_detail)
        setupToolbar(getString(R.string.unscheduled_work))

        unScheduleDetailPresenter = UnScheduleDetailPresenter(this, resources)

        intent.extras?.let {
            if (it.containsKey(ScheduleMainFragment.ARG_SCHEDULE_DETAIL)) {
                scheduleDetail =
                    it.getParcelable(ScheduleMainFragment.ARG_SCHEDULE_DETAIL) as ScheduleDetail?

                initializeUI()
                unScheduleDetailPresenter.getScheduleDetail(scheduleDetail?.scheduleIdentity!!)
            }
        }
    }

    private fun initializeUI() {
        recyclerViewLiveLoad.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            liveLoadsAdapter = UnScheduledWorkItemAdapter(
                resources, getString(R.string.string_live_loads),
                adapterItemClickListener = this@UnScheduleDetailActivity
            )
            adapter = liveLoadsAdapter
        }

        recyclerViewDrops.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            dropsAdapter = UnScheduledWorkItemAdapter(
                resources, getString(R.string.string_drops),
                adapterItemClickListener = this@UnScheduleDetailActivity
            )
            adapter = dropsAdapter
        }

        recyclerViewOutBonds.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            outBondsAdapter = UnScheduledWorkItemAdapter(
                resources, getString(R.string.string_out_bonds),
                adapterItemClickListener = this@UnScheduleDetailActivity
            )
            adapter = outBondsAdapter
        }

        scheduleDetail?.let { scheduleDetail ->
            textViewBuildingName.text = scheduleDetail.buildingName
            scheduleDetail.startDate?.let {
                textViewScheduleDate.text =
                    DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
            }
            textViewScheduleType.text = scheduleDetail.scheduleTypeNames
            textViewWorkItemsCount.text = String.format(
                getString(R.string.work_items_count),
                scheduleDetail.totalNumberOfWorkItems
            )
        }

        recyclerViewLumpersImagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(OverlapDecoration())
            allLumpersImagesAdapter =
                LumperImagesAdapter(ArrayList(), this@UnScheduleDetailActivity)
            adapter = allLumpersImagesAdapter
        }
    }

    private fun clearAllData() {
        textViewBuildingName.visibility = View.INVISIBLE
        textViewScheduleDate.visibility = View.INVISIBLE
        textViewScheduleType.visibility = View.INVISIBLE
        textViewWorkItemsCount.visibility = View.INVISIBLE
        recyclerViewLumpersImagesList.visibility = View.GONE
        layoutAllWorkItems.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            unScheduleDetailPresenter.getScheduleDetail(scheduleDetail?.scheduleIdentity!!)

            data?.also {
                val isScheduleStatusChanged =
                    data.getBooleanExtra(ARG_IS_SCHEDULED_STATUS_CHANGED, false)
                val intent = Intent()
                intent.putExtra(ARG_IS_SCHEDULED_STATUS_CHANGED, isScheduleStatusChanged)
                setResult(RESULT_OK, intent)
            } ?: run {
                setResult(RESULT_OK)
            }
        }
    }

    /*
    * Presenter Listeners
    */
    override fun showScheduleData(scheduleDetail: ScheduleDetail) {
        this.scheduleDetail = scheduleDetail
        textViewBuildingName.text = scheduleDetail.buildingName
        scheduleDetail.startDate?.let {
            textViewScheduleDate.text =
                DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = scheduleDetail.scheduleTypeNames
        textViewWorkItemsCount.text = String.format(
            getString(R.string.work_items_count),
            scheduleDetail.totalNumberOfWorkItems
        )

        val allLumpersList = ArrayList<EmployeeData>()

        scheduleDetail.scheduleTypes?.let { scheduleTypes ->

            // Show LiveLoads work Items Listing
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
        clearAllData()
        SnackBarFactory.createSnackBar(activity,
            mainConstraintLayout, message, getString(R.string.goBack),
            View.OnClickListener {
                onBackPressed()
            })
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    override fun onWorkItemClick(workItemDetail: WorkItemDetail, workItemTypeDisplayName: String) {
        val bundle = Bundle()
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
        bundle.putString(ARG_WORK_ITEM_TYPE, workItemDetail.workItemType)
        bundle.putString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, workItemTypeDisplayName)
        startIntent(
            UnScheduledWorkItemDetailActivity::class.java,
            bundle = bundle,
            requestCode = AppConstant.REQUEST_CODE_CHANGED
        )
    }

    override fun onAddLumpersItemClick(workItemDetail: WorkItemDetail) {
        val bundle = Bundle()
        bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
        bundle.putString(ARG_WORK_ITEM_TYPE, workItemDetail.workItemType)
        startIntent(
            AddWorkItemLumpersActivity::class.java,
            bundle = bundle,
            requestCode = AppConstant.REQUEST_CODE_CHANGED
        )
    }

    override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }
}