package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogWarningContract
import com.quickhandslogistics.modified.contracts.schedule.WorkItemDetailContract
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.WorkItemDetailAdapter
import com.quickhandslogistics.modified.views.fragments.InfoWarningDialogFragment
import kotlinx.android.synthetic.main.activity_work_item_detail.*

class WorkItemDetailActivity : BaseActivity(),
    WorkItemDetailContract.View.OnAdapterItemClickListener {

    private var workItemDetailAdapter: WorkItemDetailAdapter? = null

    companion object {
        const val ARG_CAN_REPLACE = "ARG_CAN_REPLACE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_item_detail)
        setupToolbar()

        intent.extras?.let { it ->
            if (it.containsKey(ARG_CAN_REPLACE)) {
                val canReplace = it.getBoolean(ARG_CAN_REPLACE, true)

                recyclerViewLumpers.apply {
                    val linearLayoutManager = LinearLayoutManager(this@WorkItemDetailActivity)
                    layoutManager = linearLayoutManager
                    val dividerItemDecoration =
                        DividerItemDecoration(activity, linearLayoutManager.orientation)
                    addItemDecoration(dividerItemDecoration)
                    workItemDetailAdapter =
                        WorkItemDetailAdapter(this@WorkItemDetailActivity, canReplace)
                    adapter = workItemDetailAdapter
                }
            }
        }
    }

    override fun onReplaceItemClick(position: Int) {
        val dialog = InfoWarningDialogFragment.newInstance(
            getString(R.string.string_ask_new_lumper),
            positiveButtonText = getString(R.string.string_yes),
            negativeButtonText = getString(R.string.string_no),
            onClickListener = object : InfoDialogWarningContract.View.OnClickListener {
                override fun onPositiveButtonClick() {
                    workItemDetailAdapter?.updateReplacePosition(position)
                }

                override fun onNegativeButtonClick() {
                }
            })
        dialog.show(supportFragmentManager, InfoWarningDialogFragment::class.simpleName)
    }
}
