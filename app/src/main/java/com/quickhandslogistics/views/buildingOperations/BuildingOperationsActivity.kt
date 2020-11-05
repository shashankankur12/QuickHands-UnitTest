package com.quickhandslogistics.views.buildingOperations

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.buildingOperations.BuildingOperationsAdapter
import com.quickhandslogistics.contracts.buildingOperations.BuildingOperationsContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.presenters.buildingOperations.BuildingOperationsPresenter
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import kotlinx.android.synthetic.main.content_building_operations.*

class BuildingOperationsActivity : BaseActivity(), View.OnClickListener, BuildingOperationsContract.View,
    BuildingOperationsContract.View.OnAdapterItemClickListener {

    private var workItemId: String = ""
    private var parameters: ArrayList<String> = ArrayList()

    private lateinit var buildingOperationsPresenter: BuildingOperationsPresenter
    private var buildingOperationsAdapter: BuildingOperationsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_operations)
        setupToolbar(getString(R.string.edit_parameters))

        intent.extras?.let { it ->
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            parameters = it.getStringArrayList(ARG_BUILDING_PARAMETERS) as ArrayList<String>
        }

        initializeUI()

        buildingOperationsPresenter = BuildingOperationsPresenter(this, resources)
        buildingOperationsPresenter.fetchBuildingOperationDetails(workItemId)
    }

    override fun onDestroy() {
        super.onDestroy()
        buildingOperationsPresenter.onDestroy()
    }

    private fun initializeUI() {
        buttonSubmit.setOnClickListener(this)
        buttonCancelRequest.setOnClickListener(this)

        recyclerViewBuildingOperations.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(20, 20))
            buildingOperationsAdapter = BuildingOperationsAdapter(parameters, this@BuildingOperationsActivity)
            adapter = buildingOperationsAdapter
        }

        buildingOperationsAdapter!!.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

    }

    private fun submitBODetails() {
        val data = buildingOperationsAdapter?.getUpdatedData()
        data?.let {
//            CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
//                override fun onConfirmClick() {
                    buildingOperationsPresenter.saveBuildingOperationsData(workItemId, data)
//                }
//
//                override fun onCancelClick() {
//                }
//            })
        }
    }

    private fun invalidateEmptyView() {
        if (buildingOperationsAdapter!!.itemCount == 0) {
            isDataSave(true)
        } else{
            if ( buildingOperationsAdapter!!.getUpdatedData().size>0)
                isDataSave(false)
            else isDataSave(true)
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSubmit.id -> submitBODetails()
                buttonCancelRequest.id -> onBackPressed()
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
        isDataSave(true)
        onBackPressed()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onTextChanged() {
        if (buildingOperationsAdapter!!.compareChanges()) {
            isDataSave(true)
        } else isDataSave(false)
    }
}