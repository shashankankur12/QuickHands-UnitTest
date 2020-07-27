package com.quickhandslogistics.views.schedule

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.schedule.AddWorkItemLumperAdapter
import com.quickhandslogistics.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.schedule.AddWorkItemLumpersPresenter
import com.quickhandslogistics.utils.AppUtils
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*

class AddWorkItemLumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    AddWorkItemLumpersContract.View, AddWorkItemLumpersContract.View.OnAdapterItemClickListener {

    private var workItemId = ""
    private var workItemType = ""
    private var assignedLumpersList: ArrayList<EmployeeData> = ArrayList()
    private var permanentLumpersList: ArrayList<EmployeeData> = ArrayList()
    private var temporaryLumpersList: ArrayList<EmployeeData> = ArrayList()

    private lateinit var addWorkItemLumpersPresenter: AddWorkItemLumpersPresenter
    private lateinit var addWorkItemLumperAdapter: AddWorkItemLumperAdapter

    companion object {
        const val ARG_IS_ADD_LUMPER = "ARG_IS_ADD_LUMPER"
        const val ARG_ASSIGNED_LUMPERS_LIST = "ARG_ASSIGNED_LUMPERS_LIST"
        const val PERMANENT_LUMPERS_LIST = "PERMANENT_LUMPERS_LIST"
        const val TEMP_LUMPERS_LIST = "TEMP_LUMPERS_LIST"
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
        savedInstanceState?.also {
            if (savedInstanceState.containsKey(TEMP_LUMPERS_LIST)) {
                temporaryLumpersList =
                    savedInstanceState.getParcelableArrayList(TEMP_LUMPERS_LIST)!!
            }
            if (savedInstanceState.containsKey(PERMANENT_LUMPERS_LIST)) {
                permanentLumpersList =
                    savedInstanceState.getParcelableArrayList(PERMANENT_LUMPERS_LIST)!!
                showLumpersData(permanentLumpersList, temporaryLumpersList)
            }
        } ?: run {
            addWorkItemLumpersPresenter.fetchLumpersList()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (permanentLumpersList != null)
            outState.putParcelableArrayList(PERMANENT_LUMPERS_LIST, permanentLumpersList)
        if (temporaryLumpersList != null)
            outState.putParcelableArrayList(TEMP_LUMPERS_LIST, temporaryLumpersList)
        super.onSaveInstanceState(outState)
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
                invalidateEmptyView()
            }
        })

        // Enable Submit button if Lead is Updating Lumpers
        onSelectLumper(assignedLumpersList.size)

        buttonAdd.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (addWorkItemLumperAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (addWorkItemLumperAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_add_work_item_lumpers_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_add_work_item_lumpers_info_message)
        }
    }

    private fun showConfirmationDialog() {
        val selectedLumperIdsList = addWorkItemLumperAdapter.getSelectedLumper()
        if (selectedLumperIdsList.size > 0) {
            CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    assignLumpersToWorkItem(selectedLumperIdsList)
                }

                override fun onCancelClick() {
                }
            })
        }
    }

    private fun assignLumpersToWorkItem(selectedLumperIdsList: ArrayList<String>) {
        val tempLumperIds = ArrayList<String>()
        for (lumper in temporaryLumpersList) {
            if (selectedLumperIdsList.contains(lumper.id!!)) {
                tempLumperIds.add(lumper.id!!)
                selectedLumperIdsList.remove(lumper.id!!)
            }
        }
        addWorkItemLumpersPresenter.initiateAssigningLumpers(selectedLumperIdsList, tempLumperIds, workItemId, workItemType)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAdd.id -> showConfirmationDialog()
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

    override fun showLumpersData(permanentLumpers: ArrayList<EmployeeData>, temporaryLumpers: ArrayList<EmployeeData>) {
        this.permanentLumpersList = permanentLumpers
        this.temporaryLumpersList = temporaryLumpers

        val allLumpersList = ArrayList<EmployeeData>()
        allLumpersList.addAll(permanentLumpers)
        allLumpersList.addAll(temporaryLumpers)

        addWorkItemLumperAdapter.updateLumpersData(allLumpersList)
    }

    override fun lumperAssignmentFinished() {
        setResult(RESULT_OK)
        onBackPressed()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */
    override fun onSelectLumper(totalSelectedCount: Int) {
        buttonAdd.isEnabled = totalSelectedCount > 0
    }
}
