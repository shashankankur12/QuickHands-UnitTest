package com.quickhandslogistics.views.buildingOperations

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.buildingOperations.BuildingOperationsAdapter
import com.quickhandslogistics.contracts.buildingOperations.BuildingOperationsContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.presenters.buildingOperations.BuildingOperationsPresenter
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.content_building_operations.*

class BuildingOperationsActivity : BaseActivity(), View.OnClickListener, BuildingOperationsContract.View {

    private var workItemId: String = ""
    private var parameters: ArrayList<String> = ArrayList()

    private lateinit var buildingOperationsPresenter: BuildingOperationsPresenter
    private var buildingOperationsAdapter: BuildingOperationsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_operations)
        setupToolbar(getString(R.string.update_building_operations))

        intent.extras?.let { it ->
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            parameters = it.getStringArrayList(ARG_BUILDING_PARAMETERS) as ArrayList<String>
        }

        initializeUI()

        buildingOperationsPresenter = BuildingOperationsPresenter(this, resources)
        buildingOperationsPresenter.fetchBuildingOperationDetails(workItemId)
    }

    private fun initializeUI() {
        buttonSubmit.setOnClickListener(this)

        recyclerViewBuildingOperations.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(20, 20))
            buildingOperationsAdapter = BuildingOperationsAdapter(parameters)
            adapter = buildingOperationsAdapter
        }
    }

    private fun submitBODetails() {
        val data = buildingOperationsAdapter?.getUpdatedData()
        data?.let {
            CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    buildingOperationsPresenter.saveBuildingOperationsData(workItemId, data)
                }

                override fun onCancelClick() {
                }
            })
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSubmit.id -> submitBODetails()
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showBuildingOperationsData(data: HashMap<String, String>?) {
        data?.let {
            buildingOperationsAdapter?.updateData(data)
        }
    }

    override fun buildingOperationsDataSaved() {
        setResult(RESULT_OK)
        onBackPressed()
    }
}