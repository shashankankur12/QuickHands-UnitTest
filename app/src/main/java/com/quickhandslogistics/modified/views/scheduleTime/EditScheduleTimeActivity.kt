package com.quickhandslogistics.modified.views.scheduleTime

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.scheduleTime.EditScheduleTimeAdapter
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.modified.presenters.scheduleTime.EditScheduleTimePresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.ChooseLumpersActivity
import com.quickhandslogistics.modified.views.common.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_NOTES
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.activity_edit_schedule_time.*
import java.util.*
import kotlin.collections.ArrayList

class EditScheduleTimeActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    EditScheduleTimeContract.View, EditScheduleTimeContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0
    private var scheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduleTimeNotes: String? = null

    private lateinit var editScheduleTimePresenter: EditScheduleTimePresenter
    private lateinit var editScheduleTimeAdapter: EditScheduleTimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule_time)
        setupToolbar(getString(R.string.schedule_lumpers))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            scheduleTimeList = bundle.getParcelableArrayList<ScheduleTimeDetail>(ARG_SCHEDULED_TIME_LIST) as ArrayList<ScheduleTimeDetail>
            scheduleTimeNotes = bundle.getString(ARG_SCHEDULED_TIME_NOTES, "")
        }

        initializeUI()

        editScheduleTimePresenter = EditScheduleTimePresenter(this, resources)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menu?.findItem(R.id.actionAddLumpers)?.isVisible = true
        menu?.findItem(R.id.actionAddSameLumperTime)?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val menuItem = menu.findItem(R.id.actionAddLumpers)
            if (editScheduleTimeAdapter.itemCount > 0) {
                menuItem.title = getString(R.string.update_lumpers)
            } else {
                menuItem.title = getString(R.string.add_lumpers)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionAddSameLumperTime -> chooseSameTimeForAllLumpers()
            R.id.actionAddLumpers -> showChooseLumpersScreen()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            data?.extras?.let { bundle ->
                val employeeDataList = bundle.getParcelableArrayList<EmployeeData>(ARG_LUMPERS_LIST)
                employeeDataList?.let {
                    editScheduleTimeAdapter.addLumpersList(employeeDataList)
                }
            }
        }
    }

    private fun initializeUI() {
        editTextNotes.setText(scheduleTimeNotes)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            editScheduleTimeAdapter = EditScheduleTimeAdapter(scheduleTimeList, this@EditScheduleTimeActivity)
            adapter = editScheduleTimeAdapter
        }

        editScheduleTimeAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        textViewAddSameTime.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        invalidateOptionsMenu()

        if (scheduleTimeList.size == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            showChooseLumpersScreen()
        } else {
            textViewAddSameTime.visibility = View.VISIBLE
            buttonSubmit.isEnabled = true
        }
    }

    private fun invalidateEmptyView() {
        if (editScheduleTimeAdapter.itemCount == 0) {
            buttonSubmit.isEnabled = false
            textViewAddSameTime.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
            if (editScheduleTimeAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.string_no_record_found)
            } else {
                textViewEmptyData.text = getString(R.string.empty_edit_schedule_time)
            }
        } else {
            buttonSubmit.isEnabled = true
            textViewAddSameTime.visibility = View.VISIBLE
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_edit_schedule_time)
        }
    }

    private fun chooseSameTimeForAllLumpers() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedTime
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this, OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                editScheduleTimeAdapter.addStartTimetoAll(calendar.timeInMillis)
            }, mHour, mMinute, false
        ).show()
    }

    private fun showChooseLumpersScreen() {
        val lumpersList = editScheduleTimeAdapter.getLumpersList()
        val bundle = Bundle()
        bundle.putParcelableArrayList(ChooseLumpersActivity.ARG_ASSIGNED_LUMPERS_LIST, lumpersList)
        bundle.putParcelableArrayList(ARG_SCHEDULED_TIME_LIST, scheduleTimeList)
        startIntent(ChooseLumpersActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    private fun saveLumperScheduleTimings() {
        val scheduledLumpersIdsTimeMap = editScheduleTimeAdapter.getScheduledLumpersTimeMap()
        if (scheduledLumpersIdsTimeMap.size > 0) {
            val notes = editTextNotes.text.toString()
            showConfirmationDialog(scheduledLumpersIdsTimeMap, notes)
        }
    }

    private fun showConfirmationDialog(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String) {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                editScheduleTimePresenter.initiateScheduleTime(scheduledLumpersIdsTimeMap, notes, Date(selectedTime))
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSameTime.id -> chooseSameTimeForAllLumpers()
                buttonSubmit.id -> saveLumperScheduleTimings()
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
            editScheduleTimeAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun scheduleTimeFinished() {
        setResult(RESULT_OK)
        onBackPressed()
    }

    /** Adapter Listeners */
    override fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long) {
        val calendar = Calendar.getInstance()
        if (timeInMillis > 0) {
            calendar.timeInMillis = timeInMillis
        } else {
            calendar.timeInMillis = selectedTime
        }
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this, OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                editScheduleTimeAdapter.addStartTime(adapterPosition, calendar.timeInMillis)
            }, mHour, mMinute, false
        ).show()
    }
}