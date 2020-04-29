package com.quickhandslogistics.modified.views.workSheet

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
import com.quickhandslogistics.modified.adapters.common.ContainerDetailAdapter
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.buildingOperations.BuildingOperationsActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_bo.*

class WorkSheetItemDetailBOFragment : BaseFragment(), View.OnClickListener {

    private lateinit var containerDetailAdapter: ContainerDetailAdapter
    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? =
        null

    private var workItemDetail: WorkItemDetail? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener =
                activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_bo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewBuildingOperations.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            containerDetailAdapter = ContainerDetailAdapter()
            adapter = containerDetailAdapter
        }

        containerDetailAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
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
    }

    fun showBuildingOperationsData(workItemDetail: WorkItemDetail) {
        this.workItemDetail = workItemDetail
        containerDetailAdapter.updateData(
            workItemDetail.buildingOps,
            workItemDetail.buildingDetailData?.parameters
        )
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonUpdate.id -> {
                    val bundle = Bundle()
                    bundle.putBoolean(ARG_ALLOW_UPDATE, true)
                    bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
                    bundle.putStringArrayList(
                        ARG_BUILDING_PARAMETERS,
                        workItemDetail?.buildingDetailData?.parameters
                    )
                    startIntent(
                        BuildingOperationsActivity::class.java,
                        bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            onFragmentInteractionListener?.fetchWorkItemDetail()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WorkSheetItemDetailBOFragment()
    }
}