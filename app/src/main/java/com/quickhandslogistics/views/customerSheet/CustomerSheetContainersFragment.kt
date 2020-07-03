package com.quickhandslogistics.views.customerSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerSheet.CustomerSheetContainersAdapter
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContainersContract
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import kotlinx.android.synthetic.main.fragment_customer_sheet_containers.*
import kotlin.collections.ArrayList

class CustomerSheetContainersFragment : BaseFragment(), CustomerSheetContainersContract.View.OnAdapterItemClickListener {

    private var onFragmentInteractionListener: CustomerSheetContract.View.OnFragmentInteractionListener? = null

    private lateinit var customerSheetContainersAdapter: CustomerSheetContainersAdapter

    private var onGoingWorkItems = ArrayList<WorkItemDetail>()
    private var cancelledWorkItems = ArrayList<WorkItemDetail>()
    private var completedWorkItems = ArrayList<WorkItemDetail>()

    companion object {
        private const val ARG_ONGOING_ITEMS = "ARG_ONGOING_ITEMS"
        private const val ARG_CANCELLED_ITEMS = "ARG_CANCELLED_ITEMS"
        private const val ARG_COMPLETED_ITEMS = "ARG_COMPLETED_ITEMS"

        @JvmStatic
        fun newInstance(allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>?) =
            CustomerSheetContainersFragment().apply {
                if (allWorkItemLists != null) {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_ONGOING_ITEMS, allWorkItemLists.first)
                        putParcelableArrayList(ARG_CANCELLED_ITEMS, allWorkItemLists.second)
                        putParcelableArrayList(ARG_COMPLETED_ITEMS, allWorkItemLists.third)
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is CustomerSheetContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = parentFragment as CustomerSheetContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            onGoingWorkItems = it.getParcelableArrayList(ARG_ONGOING_ITEMS)!!
            cancelledWorkItems = it.getParcelableArrayList(ARG_CANCELLED_ITEMS)!!
            completedWorkItems = it.getParcelableArrayList(ARG_COMPLETED_ITEMS)!!
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
            customerSheetContainersAdapter = CustomerSheetContainersAdapter(resources, sharedPref, this@CustomerSheetContainersFragment)
            adapter = customerSheetContainersAdapter
        }

        customerSheetContainersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (customerSheetContainersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        updateWorkItemsList(onGoingWorkItems, cancelledWorkItems, completedWorkItems)
    }

    fun updateWorkItemsList(onGoingWorkItems: ArrayList<WorkItemDetail>, cancelledWorkItems: ArrayList<WorkItemDetail>, completedWorkItems: ArrayList<WorkItemDetail>) {
        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(completedWorkItems)
        allWorkItems.addAll(cancelledWorkItems)
        allWorkItems.addAll(onGoingWorkItems)

        customerSheetContainersAdapter.updateList(allWorkItems)

        textViewCompletedCount.text = String.format(getString(R.string.completed_s), completedWorkItems.size)
        textViewCancelledCount.text = String.format(getString(R.string.cancelled_s), cancelledWorkItems.size)
        textViewUnfinishedCount.text = String.format(getString(R.string.ongoing_s), onGoingWorkItems.size)
    }

    /** Adapter Listeners */
    override fun onBOItemClick(workItemDetail: WorkItemDetail, parameters: ArrayList<String>) {
        val bundle = Bundle()
        bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, parameters)
        bundle.putSerializable(ARG_BUILDING_PARAMETER_VALUES, workItemDetail.buildingOps)
        startIntent(BuildingOperationsViewActivity::class.java, bundle = bundle)
    }

    override fun onNotesItemClick(notesQHLCustomer: String?) {
        notesQHLCustomer?.let {
            CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), notesQHLCustomer, fragmentActivity!!)
        }
    }
}