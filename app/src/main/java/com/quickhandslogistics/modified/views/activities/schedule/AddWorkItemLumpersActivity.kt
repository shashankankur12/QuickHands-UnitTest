package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.schedule.AddWorkItemLumpersPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.schedule.AddWorkItemLumperAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*

class AddWorkItemLumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    AddWorkItemLumpersContract.View, AddWorkItemLumpersContract.View.OnAdapterItemClickListener {

    private var workItemId = ""
    private var workItemType = ""
    private var assignedLumpersList: ArrayList<EmployeeData> = ArrayList()

    private lateinit var addWorkItemLumpersPresenter: AddWorkItemLumpersPresenter
    private lateinit var addWorkItemLumperAdapter: AddWorkItemLumperAdapter

    private var progressDialog: Dialog? = null

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
                assignedLumpersList =
                    it.getParcelableArrayList<EmployeeData>(ARG_ASSIGNED_LUMPERS_LIST) as ArrayList<EmployeeData>
            }

            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")

            if (isAddLumper) {
                setupToolbar(getString(R.string.add_lumpers))
            } else {
                setupToolbar(getString(R.string.update_lumpers))
            }
        }

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            addWorkItemLumperAdapter =
                AddWorkItemLumperAdapter(
                    assignedLumpersList,
                    this@AddWorkItemLumpersActivity
                )
            adapter = addWorkItemLumperAdapter
        }

        addWorkItemLumperAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (addWorkItemLumperAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        // Enable Submit button if Lead is Updating Lumpers
        onSelectLumper(assignedLumpersList.size)

        addWorkItemLumpersPresenter = AddWorkItemLumpersPresenter(this, resources, sharedPref)
        addWorkItemLumpersPresenter.fetchLumpersList()

        buttonAdd.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAdd.id -> {
                    val selectedLumperIdsList = addWorkItemLumperAdapter.getSelectedLumper()
                    if (selectedLumperIdsList.size > 0) {
                        addWorkItemLumpersPresenter.initiateAssigningLumpers(
                            selectedLumperIdsList, workItemId, workItemType
                        )
                    }
                }

                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(activity)
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

    override fun onSelectLumper(totalSelectedCount: Int) {
        if (totalSelectedCount > 0) {
            buttonAdd.isEnabled = true
            buttonAdd.setBackgroundResource(R.drawable.round_button_red_new)
        } else {
            buttonAdd.isEnabled = false
            buttonAdd.setBackgroundResource(R.drawable.round_button_disabled)
        }
    }

    /*
    * Presenter Listeners
    */
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
}
