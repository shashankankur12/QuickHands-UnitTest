package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.contracts.schedule.WorkItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.ScheduledWorkItemDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.ChooseLumperActivity
import com.quickhandslogistics.modified.views.adapters.schedule.ScheduledWorkItemDetailAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.activity_scheduled_work_item_detail.*

class ScheduledWorkItemDetailActivity : BaseActivity(), ScheduledWorkItemDetailContract.View,
    WorkItemDetailContract.View.OnAdapterItemClickListener, View.OnClickListener {

    private var workItemId: String = ""
    private var workItemType: String = ""
    private var workItemTypeDisplayName: String = ""
    private var allowUpdate: Boolean = true
    private var workItemDetail: WorkItemDetail? = null

    private lateinit var lumpersAdapter: ScheduledWorkItemDetailAdapter
    private lateinit var scheduledWorkItemDetailPresenter: ScheduledWorkItemDetailPresenter

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_work_item_detail)
        setupToolbar(getString(R.string.scheduled_work_details))

        intent.extras?.let { it ->
            allowUpdate = it.getBoolean(ARG_ALLOW_UPDATE, true)
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")
            workItemTypeDisplayName = it.getString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, "")
        }

        initializeUI()

        scheduledWorkItemDetailPresenter =
            ScheduledWorkItemDetailPresenter(this, resources)
        scheduledWorkItemDetailPresenter.fetchWorkItemDetail(workItemId)

        buttonAddBuildingOperations.setOnClickListener(this)
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(this@ScheduledWorkItemDetailActivity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter =
                ScheduledWorkItemDetailAdapter(this@ScheduledWorkItemDetailActivity, allowUpdate)
            adapter = lumpersAdapter
        }

        if (allowUpdate) {
            buttonAddBuildingOperations.text = getString(R.string.add_building_operations)
        } else {
            buttonAddBuildingOperations.text = getString(R.string.building_operations)
        }
    }

    override fun onReplaceItemClick(position: Int) {
        startIntent(ChooseLumperActivity::class.java)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAddBuildingOperations.id -> {
                    if (!workItemDetail?.buildingDetailData?.parameters.isNullOrEmpty()) {
                        val bundle = Bundle()
                        bundle.putBoolean(ARG_ALLOW_UPDATE, allowUpdate)
                        bundle.putStringArrayList(
                            ARG_BUILDING_PARAMETERS,
                            workItemDetail?.buildingDetailData?.parameters
                        )
                        startIntent(BuildingOperationsActivity::class.java, bundle = bundle)
                    }
                }
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
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showWorkItemDetail(workItemDetail: WorkItemDetail) {
        this.workItemDetail = workItemDetail
        textViewStartTime.text = String.format(
            getString(R.string.start_time_container),
            workItemDetail.startTime
        )
        workItemDetail.scheduledFrom?.let {
            textViewScheduledDate.text =
                DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = workItemTypeDisplayName

        when (workItemTypeDisplayName) {
            getString(R.string.string_drops) -> {
                textViewWorkItemsCount.text = String.format(
                    getString(R.string.no_of_drops),
                    workItemDetail.numberOfDrops
                )
            }
            getString(R.string.string_live_loads) -> {
                textViewWorkItemsCount.text = String.format(
                    resources.getString(R.string.live_load_sequence),
                    workItemDetail.sequence
                )
            }
            else -> {
                textViewWorkItemsCount.text = String.format(
                    resources.getString(R.string.outbound_sequence),
                    workItemDetail.sequence
                )
            }
        }

        workItemDetail.assignedLumpersList?.let { assignedLumpersList ->
            lumpersAdapter.updateData(assignedLumpersList)
        }
    }
}
