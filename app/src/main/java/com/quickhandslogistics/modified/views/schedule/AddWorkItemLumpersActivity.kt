package com.quickhandslogistics.modified.views.schedule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.schedule.AddWorkItemLumperAdapter
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.schedule.AddWorkItemLumpersPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.AppUtils
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*

class AddWorkItemLumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    AddWorkItemLumpersContract.View, AddWorkItemLumpersContract.View.OnAdapterItemClickListener {

    private var workItemId = ""
    private var workItemType = ""
    private var assignedLumpersList: ArrayList<EmployeeData> = ArrayList()

    private lateinit var addWorkItemLumpersPresenter: AddWorkItemLumpersPresenter
    private lateinit var addWorkItemLumperAdapter: AddWorkItemLumperAdapter

    companion object {
        const val ARG_IS_ADD_LUMPER = "ARG_IS_ADD_LUMPER"
        const val ARG_ASSIGNED_LUMPERS_LIST = "ARG_ASSIGNED_LUMPERS_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work_item_lumpers)

        intent.extras?.let { it ->
            val isAddLumper = it.getBoolean(ARG_IS_ADD_LUMPER, true)
            if (it.containsKey(ARG_ASSIGNED_LUMPERS_LIST)) {
                assignedLumpersList = it.getParcelableArrayList<EmployeeData>(ARG_ASSIGNED_LUMPERS_LIST) as ArrayList<EmployeeData>
            }

            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")

            if (isAddLumper) {
                setupToolbar(getString(R.string.add_lumpers))
            } else {
                setupToolbar(getString(R.string.update_lumpers))
            }
        }

        initializeUI()

        addWorkItemLumpersPresenter = AddWorkItemLumpersPresenter(this, resources, sharedPref)
        addWorkItemLumpersPresenter.fetchLumpersList()
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            addWorkItemLumperAdapter = AddWorkItemLumperAdapter(assignedLumpersList, this@AddWorkItemLumpersActivity)
            adapter = addWorkItemLumperAdapter
        }

        addWorkItemLumperAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (addWorkItemLumperAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        // Enable Submit button if Lead is Updating Lumpers
        onSelectLumper(assignedLumpersList.size)

        buttonAdd.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    private fun assignLumpersToWorkItem() {
        val selectedLumperIdsList = addWorkItemLumperAdapter.getSelectedLumper()
        if (selectedLumperIdsList.size > 0) {
            CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    addWorkItemLumpersPresenter.initiateAssigningLumpers(selectedLumperIdsList, workItemId, workItemType)
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
                buttonAdd.id -> assignLumpersToWorkItem()
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(activity)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            addWorkItemLumperAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        addWorkItemLumperAdapter.updateLumpersData(employeeDataList)
        if (employeeDataList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun lumperAssignmentFinished() {
        setResult(RESULT_OK)
        onBackPressed()
    }

    /** Adapter Listeners */
    override fun onSelectLumper(totalSelectedCount: Int) {
        buttonAdd.isEnabled = totalSelectedCount > 0
    }
}
