package com.quickhandslogistics.views.workSheet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.schedule.ScheduleWorkItem
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_notes.*

class WorkSheetItemDetailNotesFragment : BaseFragment(), View.OnClickListener, TextWatcher {

    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? = null

    private var workItemDetail: ScheduleWorkItem? = null
    private var isDataChanged: Boolean =false

    companion object {
        private const val NOTE_WORK_DETALS = "NOTE_WORK_DETALS"
        @JvmStatic
        fun newInstance(allWorkItem: ScheduleWorkItem?) = WorkSheetItemDetailNotesFragment()
            .apply {
                arguments = Bundle().apply {
                    if(allWorkItem!=null){
                        putParcelable(NOTE_WORK_DETALS, allWorkItem)
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workItemDetail = it.getParcelable<ScheduleWorkItem>(NOTE_WORK_DETALS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addNotesTouchListener(editTextQHLCustomerNotes)
        addNotesTouchListener(editTextQHLNotes)

        buttonSubmit.setOnClickListener(this)
        editTextQHLCustomerNotes.addTextChangedListener(this)
        editTextQHLNotes.addTextChangedListener(this)
        workItemDetail?.let { showNotesData(it) }
    }

    fun showNotesData(workItemDetail: ScheduleWorkItem) {
        this.workItemDetail = workItemDetail

        if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() && workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
            editTextQHLCustomerNotes.setText(workItemDetail.notesQHLCustomer)
        }

        if (!workItemDetail.notesQHL.isNullOrEmpty() && workItemDetail.notesQHL != AppConstant.NOTES_NOT_AVAILABLE) {
            editTextQHLNotes.setText(workItemDetail.notesQHL)
        }

        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                editTextQHLCustomerNotes.isEnabled = false
                editTextQHLNotes.isEnabled = false
                buttonSubmit.visibility = View.GONE
            } else {
                editTextQHLCustomerNotes.isEnabled = true
                editTextQHLNotes.isEnabled = true
                buttonSubmit.visibility = View.VISIBLE
            }
        }
    }

    private fun saveWorkItemNotes() {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.save_notes_alert_message), fragmentActivity!!, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                workItemDetail?.let {
                    val notesQHLCustomer = editTextQHLCustomerNotes.text.toString()
                    val notesQHL = editTextQHLNotes.text.toString()

                    onFragmentInteractionListener?.updateWorkItemNotes(notesQHLCustomer, notesQHL)
                }
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSubmit.id -> saveWorkItemNotes()
            }
        }
    }

    override fun afterTextChanged(text: Editable?) {
        if (text === editTextQHLCustomerNotes.editableText) {
            var notesQHLCustomerBefore = ""
            var notesQHLBefore = ""
            if (!workItemDetail?.notesQHLCustomer.isNullOrEmpty() && workItemDetail!!.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
                notesQHLCustomerBefore = workItemDetail!!.notesQHLCustomer!!
            }
            if (!workItemDetail?.notesQHL.isNullOrEmpty() && workItemDetail!!.notesQHL != AppConstant.NOTES_NOT_AVAILABLE)
                notesQHLBefore=workItemDetail!!.notesQHL!!
            if (!notesQHLCustomerBefore.equals(text.toString()) || !notesQHLBefore.equals(editTextQHLNotes.text.toString()))
                onFragmentInteractionListener!!.dataChanged(true)
            else onFragmentInteractionListener!!.dataChanged(false)

        } else if (text === editTextQHLNotes.editableText) {
            var notesQHLCustomerBefore = ""
            var notesQHLBefore = ""
            if (!workItemDetail?.notesQHL.isNullOrEmpty() && workItemDetail!!.notesQHL != AppConstant.NOTES_NOT_AVAILABLE) {
                notesQHLBefore =workItemDetail!!.notesQHL!!
            }
            if (!workItemDetail?.notesQHLCustomer.isNullOrEmpty() && workItemDetail!!.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE)
                notesQHLCustomerBefore = workItemDetail!!.notesQHLCustomer!!
            if (!notesQHLBefore.equals(text.toString()) || !notesQHLCustomerBefore.equals(editTextQHLCustomerNotes.text.toString()))
                onFragmentInteractionListener!!.dataChanged(true)
            else onFragmentInteractionListener!!.dataChanged(false)

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

}