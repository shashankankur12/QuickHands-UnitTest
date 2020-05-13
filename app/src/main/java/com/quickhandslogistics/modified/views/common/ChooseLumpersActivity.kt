package com.quickhandslogistics.modified.views.common

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.common.ChooseLumpersAdapter
import com.quickhandslogistics.modified.contracts.common.ChooseLumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.modified.presenters.common.ChooseLumpersPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.content_choose_lumper.*

class ChooseLumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    ChooseLumpersContract.View, ChooseLumpersContract.View.OnAdapterItemClickListener {

    private var currentPageIndex: Int = 1
    private var nextPageIndex: Int = 1
    private var totalPagesCount: Int = 1

    private var assignedLumpersList: ArrayList<EmployeeData> = ArrayList()
    private var scheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()

    private lateinit var chooseLumpersPresenter: ChooseLumpersPresenter
    private lateinit var chooseLumpersAdapter: ChooseLumpersAdapter

    companion object {
        const val ARG_ASSIGNED_LUMPERS_LIST = "ARG_ASSIGNED_LUMPERS_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_lumpers)
        setupToolbar(getString(R.string.choose_lumpers))

        intent.extras?.let { bundle ->
            assignedLumpersList = bundle.getParcelableArrayList<EmployeeData>(ARG_ASSIGNED_LUMPERS_LIST) as ArrayList<EmployeeData>
            scheduleTimeList = bundle.getParcelableArrayList<ScheduleTimeDetail>(ARG_SCHEDULED_TIME_LIST) as ArrayList<ScheduleTimeDetail>
        }

        initializeUI()

        chooseLumpersPresenter = ChooseLumpersPresenter(this, resources)
        chooseLumpersPresenter.fetchLumpersList(currentPageIndex)
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            chooseLumpersAdapter = ChooseLumpersAdapter(assignedLumpersList, scheduleTimeList, this@ChooseLumpersActivity)
            adapter = chooseLumpersAdapter
            addOnScrollListener(onScrollListener)
        }

        chooseLumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (chooseLumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        // Enable Submit button if Lead is Updating Lumpers List
        onSelectLumper(assignedLumpersList.size)

        buttonAdd.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAdd.id -> {
                    val selectedLumpersList = chooseLumpersAdapter.getSelectedLumpersList()
                    if (selectedLumpersList.isNotEmpty()) {
                        CustomProgressBar.getInstance().showWarningDialog(
                            getString(R.string.string_ask_to_choose_lumper), activity, object : CustomDialogWarningListener {
                                override fun onConfirmClick() {
                                    val intent = Intent()
                                    val bundle = Bundle()
                                    bundle.putParcelableArrayList(ARG_LUMPERS_LIST, selectedLumpersList)
                                    intent.putExtras(bundle)
                                    setResult(RESULT_OK, intent)
                                    onBackPressed()
                                }

                                override fun onCancelClick() {
                                }
                            })
                    }
                }

                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(activity)
                }
            }
        }
    }

    private val onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.layoutManager?.let { layoutManager ->
                if (layoutManager is LinearLayoutManager) {
                    val visibleItemCount: Int = layoutManager.childCount
                    val totalItemCount: Int = layoutManager.itemCount
                    val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                    if (currentPageIndex != totalPagesCount) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPageIndex = nextPageIndex
                            chooseLumpersPresenter.fetchLumpersList(currentPageIndex)
                        }
                    }
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            chooseLumpersAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSelectLumper(totalSelectedCount: Int) {
        if (totalSelectedCount > 0) {
            buttonAdd.isEnabled = true
            buttonAdd.setBackgroundResource(R.drawable.round_button_red)
        } else {
            buttonAdd.isEnabled = false
            buttonAdd.setBackgroundResource(R.drawable.round_button_disabled)
        }
    }

    /*
    * Presenter Listeners
    */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int) {
        chooseLumpersAdapter.updateLumpersData(employeeDataList, currentPageIndex)
        if (employeeDataList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }

        this.totalPagesCount = totalPagesCount
        this.nextPageIndex = nextPageIndex
    }
}