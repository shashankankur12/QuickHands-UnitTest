package com.quickhandslogistics.views.common

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.ContainerDetailAdapter
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import kotlinx.android.synthetic.main.activity_work_item_bo_detail.*

class BuildingOperationsViewActivity : BaseActivity() {

    private var parameters: ArrayList<String> = ArrayList()
    private var buildingOps = HashMap<String, String>()

    private lateinit var containerDetailAdapter: ContainerDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_item_bo_detail)
        setupToolbar(getString(R.string.building_operations))

        intent.extras?.let { it ->
            parameters = it.getStringArrayList(ARG_BUILDING_PARAMETERS) as ArrayList<String>
            val buildingOps = it.getSerializable(ARG_BUILDING_PARAMETER_VALUES) as? HashMap<String, String>

            buildingOps?.let {
                this.buildingOps.putAll(buildingOps)
            }

            recyclerViewBuildingOperations.apply {
                layoutManager = LinearLayoutManager(activity)
                containerDetailAdapter = ContainerDetailAdapter()
                adapter = containerDetailAdapter
            }
            containerDetailAdapter.updateData(this.buildingOps, parameters)
        }
    }
}
