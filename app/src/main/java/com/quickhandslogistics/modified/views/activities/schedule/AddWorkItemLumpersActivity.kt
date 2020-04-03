package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.schedule.AddWorkItemLumpersPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.schedule.AddWorkItemLumperAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*

class AddWorkItemLumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    AddWorkItemLumpersContract.View, AddWorkItemLumpersContract.View.OnAdapterItemClickListener {

    private var position: Int = 0
    private var addedLumpers: ArrayList<EmployeeData> = ArrayList()

    private var workItemId = ""
    private var workItemType = ""

    private lateinit var addWorkItemLumpersPresenter: AddWorkItemLumpersPresenter
    private lateinit var addWorkItemLumperAdapter: AddWorkItemLumperAdapter

    private var progressDialog: Dialog? = null

    companion object {
        const val ARG_IS_ADD_LUMPER = "ARG_IS_ADD_LUMPER"
        const val ARG_WORK_ITEM_ID = "ARG_WORK_ITEM_ID"
        const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work_item_lumpers)

        intent.extras?.let { it ->
            val isAddLumper = it.getBoolean(ARG_IS_ADD_LUMPER, true)
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")

            if (isAddLumper) {
                setupToolbar(getString(R.string.add_lumpers))
            } else {
                setupToolbar(getString(R.string.update_lumpers))
            }
        }

        position = intent.getIntExtra("position", 0)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            addWorkItemLumperAdapter =
                AddWorkItemLumperAdapter(
                    this@AddWorkItemLumpersActivity
                )
            adapter = addWorkItemLumperAdapter
        }

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
                    addedLumpers = addWorkItemLumperAdapter.getSelectedLumper()
                    if (addedLumpers.size > 0) {
                        addWorkItemLumpersPresenter.initiateAssigningLumpers(
                            addedLumpers, workItemId, workItemType
                        )
                        val intent = Intent()
                        intent.putExtra("position", position)
                        intent.putExtra("count", addedLumpers.size)
                        setResult(Activity.RESULT_OK, intent)
                        onBackPressed()
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
}
