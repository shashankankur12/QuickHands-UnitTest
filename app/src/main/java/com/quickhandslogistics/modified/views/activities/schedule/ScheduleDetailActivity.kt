package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.Schedules
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.ScheduleDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.schedule.ScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.content_schedule_detail.*

class ScheduleDetailActivity : BaseActivity(), LumperImagesContract.OnItemClickListener,
    ScheduleDetailContract.View, ScheduleDetailContract.View.OnAdapterItemClickListener {

    private lateinit var allLumpersImagesAdapter: LumperImagesAdapter
    private lateinit var liveLoadsAdapter: ScheduledWorkItemAdapter
    private lateinit var dropsAdapter: ScheduledWorkItemAdapter
    private lateinit var outBondsAdapter: ScheduledWorkItemAdapter
    private lateinit var scheduleDetailPresenter: ScheduleDetailPresenter

    private var allowUpdate: Boolean = false
    private var scheduleDetail: ScheduleDetail? = null

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        setupToolbar()

        scheduleDetailPresenter = ScheduleDetailPresenter(this, resources)

        intent.extras?.let {
            allowUpdate = it.getBoolean(ScheduleMainFragment.ARG_ALLOW_UPDATE)
            scheduleDetail =
                it.getParcelable(ScheduleMainFragment.ARG_SCHEDULE_DETAIL) as ScheduleDetail?

            initializeUI()
            scheduleDetailPresenter.getScheduleDetail(scheduleDetail?.scheduleIdentity!!)
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
                resources, getString(R.string.string_out_bonds),
                adapterItemClickListener = this@ScheduleDetailActivity
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
                LumperImagesAdapter(ArrayList(), this@ScheduleDetailActivity)
            adapter = allLumpersImagesAdapter
        }
    }

    /*
    * Presenter Listeners
    */
    override fun showScheduleData(scheduleDetail: Schedules) {
        val allLumpersList = ArrayList<EmployeeData>()

        if (scheduleDetail.live?.size!! > 0) {
            liveLoadsAdapter.updateData(scheduleDetail.live!!)
            layoutLiveLoadScheduleType.visibility = View.VISIBLE
            recyclerViewLiveLoad.visibility = View.VISIBLE

            for (workItem in scheduleDetail.live!!) {
                allLumpersList.addAll(workItem.assignedLumpersList!!)
            }
        } else {
            layoutLiveLoadScheduleType.visibility = View.GONE
            recyclerViewLiveLoad.visibility = View.GONE
        }

        if (scheduleDetail.drop?.size!! > 0) {
            dropsAdapter.updateData(scheduleDetail.drop!!)
            layoutDropsScheduleType.visibility = View.VISIBLE
            recyclerViewDrops.visibility = View.VISIBLE

            for (workItem in scheduleDetail.drop!!) {
                allLumpersList.addAll(workItem.assignedLumpersList!!)
            }
        } else {
            layoutDropsScheduleType.visibility = View.GONE
            recyclerViewDrops.visibility = View.GONE
        }

        if (scheduleDetail.outbounds?.size!! > 0) {
            outBondsAdapter.updateData(scheduleDetail.outbounds!!)
            layoutOutBondsScheduleType.visibility = View.VISIBLE
            recyclerViewOutBonds.visibility = View.VISIBLE

            for (workItem in scheduleDetail.outbounds!!) {
                allLumpersList.addAll(workItem.assignedLumpersList!!)
            }
        } else {
            layoutOutBondsScheduleType.visibility = View.GONE
            recyclerViewOutBonds.visibility = View.GONE
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
        workItemType: String
    ) {
        val bundle = Bundle()
        bundle.putBoolean(ScheduleMainFragment.ARG_ALLOW_UPDATE, allowUpdate)
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id!!)
        bundle.putString(ARG_WORK_ITEM_TYPE, workItemType)
        startIntent(ScheduledWorkItemDetailActivity::class.java, bundle = bundle)
    }
}