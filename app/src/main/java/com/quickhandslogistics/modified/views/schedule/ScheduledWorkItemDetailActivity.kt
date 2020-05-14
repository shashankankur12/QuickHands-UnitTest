package com.quickhandslogistics.modified.views.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.schedule.ScheduledWorkItemDetailAdapter
import com.quickhandslogistics.modified.contracts.schedule.ScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.ScheduledWorkItemDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.buildingOperations.BuildingOperationsActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.activity_scheduled_work_item_detail.*

class ScheduledWorkItemDetailActivity : BaseActivity(), View.OnClickListener, ScheduledWorkItemDetailContract.View {

    private var workItemId: String = ""
    private var workItemTypeDisplayName: String = ""
    private var allowUpdate: Boolean = true
    private var workItemDetail: WorkItemDetail? = null

    private lateinit var lumpersAdapter: ScheduledWorkItemDetailAdapter
    private lateinit var scheduledWorkItemDetailPresenter: ScheduledWorkItemDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_work_item_detail)
        setupToolbar(getString(R.string.scheduled_work_details))

        intent.extras?.let { it ->
            allowUpdate = it.getBoolean(ARG_ALLOW_UPDATE, true)
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemTypeDisplayName = it.getString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, "")
        }

        initializeUI()

        scheduledWorkItemDetailPresenter = ScheduledWorkItemDetailPresenter(this, resources)
        scheduledWorkItemDetailPresenter.fetchWorkItemDetail(workItemId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            scheduledWorkItemDetailPresenter.fetchWorkItemDetail(workItemId)
            setResult(RESULT_OK)
        }
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(this@ScheduledWorkItemDetailActivity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter = ScheduledWorkItemDetailAdapter()
            adapter = lumpersAdapter
        }

        lumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (lumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        if (allowUpdate) {
            buttonAddBuildingOperations.text = getString(R.string.update_building_operations)
            buttonUpdateLumpers.visibility = View.VISIBLE
        } else {
            buttonAddBuildingOperations.text = getString(R.string.building_operations)
            buttonUpdateLumpers.visibility = View.GONE
        }

        buttonUpdateLumpers.setOnClickListener(this)
        buttonAddBuildingOperations.setOnClickListener(this)
    }

    private fun showAddLumpersScreen() {
        workItemDetail?.let { workItemDetail ->
            val bundle = Bundle()
            bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
            bundle.putString(ARG_WORK_ITEM_TYPE, workItemDetail.workItemType)
            if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
                bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
            } else {
                bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, false)
                bundle.putParcelableArrayList(AddWorkItemLumpersActivity.ARG_ASSIGNED_LUMPERS_LIST, workItemDetail.assignedLumpersList)
            }
            startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
        }
    }

    private fun showBuildingOperationsScreen() {
        if (!workItemDetail?.buildingDetailData?.parameters.isNullOrEmpty()) {
            val bundle = Bundle()
            bundle.putBoolean(ARG_ALLOW_UPDATE, allowUpdate)
            bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
            bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, workItemDetail?.buildingDetailData?.parameters)
            startIntent(BuildingOperationsActivity::class.java, bundle = bundle)
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonUpdateLumpers.id -> showAddLumpersScreen()
                buttonAddBuildingOperations.id -> showBuildingOperationsScreen()
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showWorkItemDetail(workItemDetail: WorkItemDetail) {
        this.workItemDetail = workItemDetail
        textViewStartTime.text = String.format(getString(R.string.start_time_container), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))
        workItemDetail.scheduledFrom?.let {
            textViewScheduledDate.text = DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = workItemTypeDisplayName

        when (workItemTypeDisplayName) {
            getString(R.string.string_drops) -> textViewWorkItemsCount.text = String.format(getString(R.string.no_of_drops), workItemDetail.numberOfDrops)
            getString(R.string.string_live_loads) -> textViewWorkItemsCount.text = String.format(getString(R.string.live_load_sequence), workItemDetail.sequence)
            else -> textViewWorkItemsCount.text = String.format(getString(R.string.outbound_sequence), workItemDetail.sequence)
        }

        workItemDetail.assignedLumpersList?.let { assignedLumpersList ->
            lumpersAdapter.updateData(assignedLumpersList)

            if (assignedLumpersList.size > 0) {
                buttonUpdateLumpers.text = getString(R.string.update_lumpers)
            } else {
                buttonUpdateLumpers.text = getString(R.string.add_lumpers)
            }
        }
    }
}