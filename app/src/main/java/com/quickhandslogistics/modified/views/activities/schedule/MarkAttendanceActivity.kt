package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.MarkAttendanceAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import com.quickhandslogistics.utils.DateUtils
import kotlinx.android.synthetic.main.content_mark_attendance.*

class MarkAttendanceActivity : BaseActivity(), View.OnClickListener, TextWatcher {

    private var isCurrentDate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance)
        setupToolbar(getString(R.string.mark_attendance))

        intent.extras?.let {
            if (it.containsKey(ScheduleFragment.ARG_SELECTED_TIME)) {
                val selectedTime = it.getLong(ScheduleFragment.ARG_SELECTED_TIME)
                isCurrentDate = DateUtils.isCurrentDate(selectedTime)
            }
        }

        initializeUI()
    }

    private fun initializeUI() {

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            adapter = MarkAttendanceAdapter(activity)
        }

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    /*
    * Native Views Listeners
    */
    override fun onClick(view: View?) {
        view?.let {
            /*when (view.id) {
                imageViewClose.id -> {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                    bottomSheetBackground.visibility = View.GONE
                }
                button_submit.id -> {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                    bottomSheetBackground.visibility = View.GONE
                }
            }*/
        }
    }

    /*
    * Adapter Item Click Listeners
    */

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}
