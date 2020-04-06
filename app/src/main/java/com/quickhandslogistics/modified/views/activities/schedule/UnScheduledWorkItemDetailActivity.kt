package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.UnScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.UnScheduledWorkItemDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduledWorkItemDetailAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.activity_unscheduled_work_item_detail.*

class UnScheduledWorkItemDetailActivity : BaseActivity(), View.OnClickListener,
    UnScheduledWorkItemDetailContract.View {

    private var workItemId: String = ""
    private var workItemType: String = ""

    private lateinit var lumpersAdapter: UnScheduledWorkItemDetailAdapter
    private lateinit var unScheduledWorkItemDetailPresenter: UnScheduledWorkItemDetailPresenter

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unscheduled_work_item_detail)
        setupToolbar()

        intent.extras?.let {
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")
        }

        initializeUI()

        unScheduledWorkItemDetailPresenter =
            UnScheduledWorkItemDetailPresenter(this, resources, sharedPref)
        unScheduledWorkItemDetailPresenter.fetchWorkItemDetail(workItemId)

        buttonUpdate.setOnClickListener(this)
        buttonScheduleNow.setOnClickListener(this)
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(this@UnScheduledWorkItemDetailActivity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter = UnScheduledWorkItemDetailAdapter()
            adapter = lumpersAdapter
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonUpdate.id -> {
                    val bundle = Bundle()
                    bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, false)
                    startIntent(
                        AddWorkItemLumpersActivity::class.java,
                        bundle = bundle,
                        requestCode = AppConstant.REQUEST_CODE_CHANGED
                    )
                }
                buttonScheduleNow.id -> unScheduledWorkItemDetailPresenter.changeWorkItemStatus(
                    workItemId, workItemType
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            unScheduledWorkItemDetailPresenter.fetchWorkItemDetail(workItemId)
            setResult(RESULT_OK)
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

    override fun workItemStatusChanged() {
        onBackPressed()
    }

    override fun showWorkItemDetail(workItemDetail: WorkItemDetail) {
        textViewStartTime.text = String.format(
            getString(R.string.start_time_container),
            workItemDetail.startTime
        )
        workItemDetail.startDate?.let {
            textViewScheduledDate.text =
                DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }
        textViewScheduleType.text = workItemType

        when (workItemType) {
            getString(R.string.string_drops) -> {
                textViewWorkItemsCount.text = String.format(
                    getString(R.string.no_of_drops),
                    workItemDetail.numberOfDrops
                )
            }
            else -> {
                textViewWorkItemsCount.text = String.format(
                    getString(R.string.sequence),
                    workItemDetail.sequence
                )
            }
        }

        workItemDetail.assignedLumpersList?.let {
            lumpersAdapter.updateData(it)
        }
    }
}