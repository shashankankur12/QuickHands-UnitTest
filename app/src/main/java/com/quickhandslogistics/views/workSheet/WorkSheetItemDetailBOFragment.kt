package com.quickhandslogistics.views.workSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.ContainerDetailAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.buildingOperations.BuildingOperationsActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.ConnectionDetector
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_bo.*

class WorkSheetItemDetailBOFragment : BaseFragment(), View.OnClickListener {

    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? = null

    private lateinit var containerDetailAdapter: ContainerDetailAdapter

    private var workItemDetail: WorkItemContainerDetails? = null
    private var buildingDetailData: ArrayList<String>? = null

    companion object {
        private const val WORK_DETALS = "WORK_DETALS"
        private const val BUILDING_PARAMETER = "BUILDING_PARAMETER"
        @JvmStatic
        fun newInstance(allWorkItem: WorkItemContainerDetails?, buildingParams: ArrayList<String>?) = WorkSheetItemDetailBOFragment()
            .apply {
                arguments = Bundle().apply {
                    if(allWorkItem!=null){
                        putParcelable(WORK_DETALS, allWorkItem)
                        putStringArrayList(BUILDING_PARAMETER, buildingParams)
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workItemDetail = it.getParcelable<WorkItemContainerDetails>(WORK_DETALS)
            buildingDetailData = it.getStringArrayList(BUILDING_PARAMETER)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_bo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewBuildingOperations.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            containerDetailAdapter = ContainerDetailAdapter()
            adapter = containerDetailAdapter
        }

        containerDetailAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (containerDetailAdapter.itemCount == 0) {
                    textViewEmptyData.visibility = View.VISIBLE
                    buttonUpdate.isEnabled = false
                } else {
                    textViewEmptyData.visibility = View.GONE
                    buttonUpdate.isEnabled = true
                }
            }
        })

        buttonUpdate.setOnClickListener(this)
        workItemDetail?.let { showBuildingOperationsData(it, buildingDetailData) }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            onFragmentInteractionListener?.fetchWorkItemDetail(changeResultCode = true)
        }
    }

    fun showBuildingOperationsData(
        workItemDetail: WorkItemContainerDetails,
        buildingParams: ArrayList<String>?
    ) {
        this.workItemDetail = workItemDetail
        buildingDetailData = buildingParams

        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                buttonUpdate.visibility = View.GONE
            } else {
                buttonUpdate.visibility = View.VISIBLE
            }
        }
        if (buildingDetailData!=null)
            containerDetailAdapter.updateData(workItemDetail.buildingOps, buildingDetailData)
        else showEmptyData()

    }

    fun showEmptyData() {
        containerDetailAdapter.updateData(HashMap(), ArrayList())
        buttonUpdate.visibility = View.GONE
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                buttonUpdate.id -> {
                    buildingDetailData?.let {
                        val bundle = Bundle()
                        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
                        bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, buildingDetailData)
                        startIntent(
                            BuildingOperationsActivity::class.java,
                            bundle = bundle,
                            requestCode = AppConstant.REQUEST_CODE_CHANGED
                        )
                    }
                }
                else -> {}
            }
        }
    }
}