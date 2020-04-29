package com.quickhandslogistics.modified.views.workSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetItemAdapter
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import kotlinx.android.synthetic.main.fragment_work_sheet_item.*
import java.util.*

class WorkSheetItemFragment : BaseFragment(),
    WorkSheetItemContract.View.OnAdapterItemClickListener {

    private var workItemType: String = ""

    private lateinit var workSheetItemAdapter: WorkSheetItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewWorkSheet.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            workSheetItemAdapter =
                WorkSheetItemAdapter(workItemType, resources, this@WorkSheetItemFragment)
            adapter = workSheetItemAdapter
        }

        workSheetItemAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (workSheetItemAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })
    }

    fun updateWorkItemsList(onGoingWorkItems: ArrayList<WorkItemDetail>) {
        workSheetItemAdapter.updateList(onGoingWorkItems)
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onItemClick(workItemId: String, workItemTypeDisplayName: String) {
        val bundle = Bundle()
        bundle.putString(ARG_WORK_ITEM_ID, workItemId)
        bundle.putString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, workItemTypeDisplayName)
        startIntent(WorkSheetItemDetailActivity::class.java, bundle = bundle)
    }

    companion object {
        private const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"

        @JvmStatic
        fun newInstance(workItemType: String) =
            WorkSheetItemFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_WORK_ITEM_TYPE, workItemType)
                    }
                }
    }
}