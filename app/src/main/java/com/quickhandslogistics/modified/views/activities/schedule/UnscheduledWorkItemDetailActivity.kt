package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.UnScheduledWorkItemDetailAdapter
import kotlinx.android.synthetic.main.activity_unscheduled_work_item_detail.*

class UnscheduledWorkItemDetailActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unscheduled_work_item_detail)
        setupToolbar()

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(this@UnscheduledWorkItemDetailActivity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            adapter = UnScheduledWorkItemDetailAdapter()
        }

        buttonUpdate.setOnClickListener(this)
        buttonSchedule.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonUpdate.id -> {
                    val bundle = Bundle()
                    bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, false)
                    startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle)
                }
                buttonSchedule.id -> {
                    onBackPressed()
                }
            }
        }
    }
}