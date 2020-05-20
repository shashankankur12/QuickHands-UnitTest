package com.quickhandslogistics.modified.views.customerSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.customerSheet.CustomerSheetContainersAdapter
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContainersContract
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_customer_sheet_containers.*
import java.util.*

class CustomerSheetContainersFragment : BaseFragment(), CustomerSheetContainersContract.View.OnAdapterItemClickListener {

    private var onFragmentInteractionListener: CustomerSheetContract.View.OnFragmentInteractionListener? = null

    private lateinit var customerSheetContainersAdapter: CustomerSheetContainersAdapter

    companion object {
        @JvmStatic
        fun newInstance() = CustomerSheetContainersFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is CustomerSheetContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = parentFragment as CustomerSheetContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet_containers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewContainers.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            customerSheetContainersAdapter = CustomerSheetContainersAdapter(resources, this@CustomerSheetContainersFragment)
            adapter = customerSheetContainersAdapter
        }

        customerSheetContainersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (customerSheetContainersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })
    }

    fun updateWorkItemsList(onGoingWorkItems: ArrayList<WorkItemDetail>, cancelledWorkItems: ArrayList<WorkItemDetail>, completedWorkItems: ArrayList<WorkItemDetail>) {
        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(completedWorkItems)
        allWorkItems.addAll(cancelledWorkItems)
        allWorkItems.addAll(onGoingWorkItems)

        customerSheetContainersAdapter.updateList(allWorkItems)

        textViewCompletedCount.text = String.format(getString(R.string.completed_s), completedWorkItems.size)
        textViewCancelledCount.text = String.format(getString(R.string.cancelled_s), cancelledWorkItems.size)
        textViewUnfinishedCount.text = String.format(getString(R.string.unfinished_s), onGoingWorkItems.size)
    }

    /** Adapter Listeners */
    override fun onBOItemClick(workItemDetail: WorkItemDetail) {
        val bundle = Bundle()
        bundle.putStringArrayList(
            ARG_BUILDING_PARAMETERS, workItemDetail.buildingDetailData?.parameters
        )
        bundle.putSerializable(ARG_BUILDING_PARAMETER_VALUES, workItemDetail.buildingOps)
        startIntent(BuildingOperationsViewActivity::class.java, bundle = bundle)
    }

    override fun onNotesItemClick(notesQHLCustomer: String?) {
        notesQHLCustomer?.let {
            CustomProgressBar.getInstance().showInfoDialog(getString(R.string.string_note), notesQHLCustomer, fragmentActivity!!)
        }
    }
}