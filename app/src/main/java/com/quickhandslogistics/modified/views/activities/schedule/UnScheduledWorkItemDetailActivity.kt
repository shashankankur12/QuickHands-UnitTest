package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
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
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.activity_unscheduled_work_item_detail.*

class UnScheduledWorkItemDetailActivity : BaseActivity(), View.OnClickListener,
    UnScheduledWorkItemDetailContract.View {

    private var workItemDetail: WorkItemDetail? = null
    private var workItemType: String = ""

    private lateinit var unScheduledWorkItemDetailPresenter: UnScheduledWorkItemDetailPresenter

    private var progressDialog: Dialog? = null

    companion object {
        const val ARG_WORK_ITEM_DETAIL = "ARG_WORK_ITEM_DETAIL"
        const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unscheduled_work_item_detail)
        setupToolbar()

        intent.extras?.let {
            workItemDetail = it.getSerializable(ARG_WORK_ITEM_DETAIL) as WorkItemDetail
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")
        }

        initializeUI()

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(this@UnScheduledWorkItemDetailActivity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            adapter = UnScheduledWorkItemDetailAdapter()
        }

        unScheduledWorkItemDetailPresenter =
            UnScheduledWorkItemDetailPresenter(this, resources, sharedPref)

        buttonUpdate.setOnClickListener(this)
        buttonScheduleNow.setOnClickListener(this)
    }

    private fun initializeUI() {
        workItemDetail?.let { workItemDetail ->
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
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonUpdate.id -> {
                    val bundle = Bundle()
                    bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, false)
                    startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle)
                }
                buttonScheduleNow.id -> unScheduledWorkItemDetailPresenter.changeWorkItemStatus(
                    workItemDetail?.id!!,
                    workItemType
                )
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

    override fun workItemStatusChanged() {
        onBackPressed()
    }
}