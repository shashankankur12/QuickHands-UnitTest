package com.quickhandslogistics.modified.views.activities.buildingOperations

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.buildingOperations.BuildingOperationsContract
import com.quickhandslogistics.modified.presenters.buildingOperations.BuildingOperationsPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.schedule.BuildingOperationsAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.content_building_operations.*

class BuildingOperationsActivity : BaseActivity(), View.OnClickListener,
    BuildingOperationsContract.View {

    private lateinit var buildingOperationsPresenter: BuildingOperationsPresenter
    private var buildingOperationsAdapter: BuildingOperationsAdapter? = null
    private var allowUpdate: Boolean = true
    private var workItemId: String = ""
    private var parameters: ArrayList<String> = ArrayList()

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_operations)
        setupToolbar(getString(R.string.building_operations))

        intent.extras?.let { it ->
            allowUpdate = it.getBoolean(ARG_ALLOW_UPDATE, true)
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            parameters = it.getStringArrayList(ARG_BUILDING_PARAMETERS) as ArrayList<String>
        }

        buttonSubmit.visibility = if (allowUpdate) View.VISIBLE else View.GONE
        buttonSubmit.setOnClickListener(this)

        recyclerViewBuildingOperations.apply {
//            layoutManager = GridLayoutManager(activity, 2)
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(20, 20))
            buildingOperationsAdapter = BuildingOperationsAdapter(allowUpdate, parameters)
            adapter = buildingOperationsAdapter
        }

        buildingOperationsPresenter = BuildingOperationsPresenter(this, resources)
        buildingOperationsPresenter.fetchBuildingOperationDetails(workItemId)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSubmit.id -> {
                    val data = buildingOperationsAdapter?.getUpdatedData()
                    data?.let {
                        buildingOperationsPresenter.saveBuildingOperationsData(workItemId, data)
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(activity).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showBuildingOperationsData(data: HashMap<String, String>?) {
        data?.let {
            buildingOperationsAdapter?.updateData(data)
        }
    }

    override fun buildingOperationsDataSaved() {
        onBackPressed()
    }
}
