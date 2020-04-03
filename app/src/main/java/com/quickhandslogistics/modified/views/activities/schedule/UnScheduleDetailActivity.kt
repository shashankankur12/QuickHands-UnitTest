package com.quickhandslogistics.modified.views.activities.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.schedule.UnScheduledWorkItemDetailActivity.Companion.ARG_WORK_ITEM_DETAIL
import com.quickhandslogistics.modified.views.activities.schedule.UnScheduledWorkItemDetailActivity.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import kotlinx.android.synthetic.main.content_unschedule_detail.*

class UnScheduleDetailActivity : BaseActivity(), LumperImagesContract.OnItemClickListener,
    UnScheduleDetailContract.View.OnAdapterItemClickListener {

    private var scheduleDetail: ScheduleDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unschedule_detail)
        setupToolbar()

        intent.extras?.let {
            if (it.containsKey(ScheduleMainFragment.ARG_SCHEDULE_DETAIL)) {
                scheduleDetail =
                    it.getSerializable(ScheduleMainFragment.ARG_SCHEDULE_DETAIL) as ScheduleDetail
            }
        }

        initializeUI()
    }

    private fun initializeUI() {
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

            if (scheduleDetail.scheduleTypes?.liveLoads?.size!! > 0) {
                recyclerViewLiveLoad.apply {
                    layoutManager = LinearLayoutManager(activity)
                    addItemDecoration(SpaceDividerItemDecorator(15))
                    adapter = UnScheduledWorkItemAdapter(
                        resources, getString(R.string.string_live_loads),
                        scheduleDetail.scheduleTypes?.liveLoads!!,
                        this@UnScheduleDetailActivity
                    )
                }
            } else {
                layoutLiveLoadScheduleType.visibility = View.GONE
                recyclerViewLiveLoad.visibility = View.GONE
            }

            if (scheduleDetail.scheduleTypes?.drops?.size!! > 0) {
                recyclerViewDrops.apply {
                    layoutManager = LinearLayoutManager(activity)
                    addItemDecoration(SpaceDividerItemDecorator(15))
                    adapter = UnScheduledWorkItemAdapter(
                        resources, getString(R.string.string_drops),
                        scheduleDetail.scheduleTypes?.drops!!,
                        this@UnScheduleDetailActivity
                    )
                }
            } else {
                layoutDropsScheduleType.visibility = View.GONE
                recyclerViewDrops.visibility = View.GONE
            }

            if (scheduleDetail.scheduleTypes?.outbounds?.size!! > 0) {
                recyclerViewOutBonds.apply {
                    layoutManager = LinearLayoutManager(activity)
                    addItemDecoration(SpaceDividerItemDecorator(15))
                    adapter = UnScheduledWorkItemAdapter(
                        resources, getString(R.string.string_out_bonds),
                        scheduleDetail.scheduleTypes?.outbounds!!,
                        this@UnScheduleDetailActivity
                    )
                }
            } else {
                layoutOutBondsScheduleType.visibility = View.GONE
                recyclerViewOutBonds.visibility = View.GONE
            }
        }

        recyclerViewLumpersImagesList.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val lumperImages = ArrayList<ImageData>()

            for (i in 1..7) {
                lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
            }
            addItemDecoration(OverlapDecoration())
            adapter = LumperImagesAdapter(lumperImages, this@UnScheduleDetailActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
//            val position = data?.getIntExtra("position", 0)!!
//            val count = data.getIntExtra("count", 0)
//
//            lumpersCountList[position] = count
//
//            unScheduledWorkItemAdapter.updateCount(lumpersCountList)
//        }
    }

    override fun onLumperImagesClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    override fun onWorkItemClick(workItemDetail: WorkItemDetail, workItemType: String) {
        val bundle = Bundle()
        bundle.putSerializable(ARG_WORK_ITEM_DETAIL, workItemDetail)
        bundle.putString(ARG_WORK_ITEM_TYPE, workItemType)
        startIntent(UnScheduledWorkItemDetailActivity::class.java, bundle = bundle)
    }

    override fun onAddLumpersItemClick(workItemDetail: WorkItemDetail) {
        val bundle = Bundle()
        bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
        bundle.putString(AddWorkItemLumpersActivity.ARG_WORK_ITEM_ID, workItemDetail.id)
        bundle.putString(AddWorkItemLumpersActivity.ARG_WORK_ITEM_TYPE, workItemDetail.workItemType)
        startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle)
    }

    override fun onLumperImageItemClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }
}