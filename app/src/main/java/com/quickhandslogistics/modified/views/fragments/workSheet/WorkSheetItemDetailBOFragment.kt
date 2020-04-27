package com.quickhandslogistics.modified.views.fragments.workSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.buildingOperations.BuildingOperationsActivity
import com.quickhandslogistics.modified.views.adapters.ContainerDetailAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_BUILDING_PARAMETERS
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_bo.*

class WorkSheetItemDetailBOFragment : BaseFragment(), View.OnClickListener {

    private lateinit var containerDetailAdapter: ContainerDetailAdapter

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

        buttonUpdate.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonUpdate.id -> {
                    val bundle = Bundle()
                    bundle.putBoolean(ARG_ALLOW_UPDATE, true)
                    /*bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
                    */
                    bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, ArrayList())
                    startIntent(BuildingOperationsActivity::class.java, bundle = bundle)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkSheetItemDetailBOFragment()
    }
}