package com.quickhandslogistics.modified.views.lumperSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.lumperSheet.LumperSheetAdapter
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.modified.presenters.lumperSheet.LumperSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*
import java.util.*

class LumperSheetFragment : BaseFragment(), LumperSheetContract.View, TextWatcher, View.OnClickListener,
    LumperSheetContract.View.OnAdapterItemClickListener, CalendarUtils.CalendarSelectionListener {

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>

    private lateinit var lumperSheetAdapter: LumperSheetAdapter
    private lateinit var lumperSheetPresenter: LumperSheetPresenter

    companion object {
        const val ARG_LUMPER_INFO = "ARG_LUMPER_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumperSheetPresenter = LumperSheetPresenter(this, resources)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lumper_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpersSheet.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumperSheetAdapter = LumperSheetAdapter(resources, this@LumperSheetFragment)
            adapter = lumperSheetAdapter
        }

        lumperSheetAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (lumperSheetAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarLumperSheet, availableDates, this)
        singleRowCalendarLumperSheet.select(availableDates.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        lumperSheetPresenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
        }
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(
            getString(R.string.string_ask_to_submit_lumper_sheet), fragmentActivity!!, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    lumperSheetPresenter.initiateSheetSubmission(Date(selectedTime))
                }

                override fun onCancelClick() {
                }
            })
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonSubmit.id -> showConfirmationDialog()
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperSheetAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showDateString(dateString: String) {
        textViewDate.text = dateString
    }

    override fun showLumperSheetData(lumperInfoList: ArrayList<LumpersInfo>, sheetSubmitted: Boolean, selectedDate: Date) {
        selectedTime = selectedDate.time

        var isSignatureLeft = 0
        for (lumperInfo in lumperInfoList) {
            if (!ValueUtils.getDefaultOrValue(lumperInfo.sheetSigned)) {
                isSignatureLeft++
            }
        }

        if (DateUtils.isCurrentDate(selectedTime) && lumperInfoList.size > 0) {
            buttonSubmit.visibility = if (!sheetSubmitted) View.VISIBLE else View.GONE
            buttonSubmit.isEnabled = !sheetSubmitted && isSignatureLeft == 0
        } else {
            buttonSubmit.visibility = View.GONE
        }

        lumperSheetAdapter.updateLumperSheetData(lumperInfoList)
        if (lumperInfoList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpersSheet.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersSheet.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun sheetSubmittedSuccessfully() {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.lumper_sheet_submitted_successfully), fragmentActivity!!, object : CustomDialogListener {
            override fun onConfirmClick() {
                lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
            }
        })
    }

    /** Adapter Listeners */
    override fun onItemClick(lumperInfo: LumpersInfo) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_LUMPER_INFO, lumperInfo)
        bundle.putLong(ScheduleMainFragment.ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(LumperWorkDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date) {
        lumperSheetPresenter.getLumpersSheetByDate(date)
    }
}