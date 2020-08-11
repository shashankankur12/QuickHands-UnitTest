package com.quickhandslogistics.views.customerSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.customerSheet.LocalCustomerSheetData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.common.AddSignatureActivity
import kotlinx.android.synthetic.main.fragment_customer_sheet_customer.*
import kotlinx.android.synthetic.main.fragment_customer_sheet_customer.buttonSubmit
import java.io.File
import java.util.*

class CustomerSheetCustomerFragment : BaseFragment(), View.OnClickListener, TextWatcher {

    private var onFragmentInteractionListener: CustomerSheetContract.View.OnFragmentInteractionListener? = null
    private var signatureFilePath = ""
    private var customerSheet: CustomerSheetData? = null
    private var localCustomerSheet: LocalCustomerSheetData? = null

    private var selectedTime: Long? = null
    private var inCompleteWorkItemsCount: Int = 0
    private var customerId: String = ""

    companion object {
        private const val ARG_CUSTOMER_SHEET_DATA = "ARG_CUSTOMER_SHEET_DATA"
        private const val ARG_SELECTED_TIME = "ARG_SELECTED_TIME"
        private const val ARG_ONGOING_WORK_ITEMS_COUNT = "ARG_ONGOING_WORK_ITEMS_COUNT"
        private const val ARG_LOCAL_CUSTOMER_SHEET_DATA = "ARG_LOCAL_CUSTOMER_SHEET_DATA"

        @JvmStatic
        fun newInstance(
            customerSheetData: CustomerSheetData?,
            selectedTime: Long?,
            listData: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>?,
            localCustomerSheet: LocalCustomerSheetData?
        ) =
            CustomerSheetCustomerFragment().apply {
                if (selectedTime != null && listData != null) {
                    arguments = Bundle().apply {
                        putParcelable(ARG_CUSTOMER_SHEET_DATA, customerSheetData)
                        putLong(ARG_SELECTED_TIME, selectedTime)
                        putInt(ARG_ONGOING_WORK_ITEMS_COUNT, listData.first.size)
                        putParcelable(ARG_LOCAL_CUSTOMER_SHEET_DATA, localCustomerSheet)
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is CustomerSheetContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = parentFragment as CustomerSheetContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerSheet = it.getParcelable(ARG_CUSTOMER_SHEET_DATA)
            selectedTime = it.getLong(ARG_SELECTED_TIME)
            inCompleteWorkItemsCount = it.getInt(ARG_ONGOING_WORK_ITEMS_COUNT)
            localCustomerSheet = it.getParcelable(ARG_LOCAL_CUSTOMER_SHEET_DATA)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addNotesTouchListener(editTextCustomerNotes)

        editTextCustomerNotes.addTextChangedListener(this)
        editTextCustomerName.addTextChangedListener(this)

        textViewAddSignature.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)

        if (selectedTime != null) {
            updateCustomerDetails(customerSheet, selectedTime!!, inCompleteWorkItemsCount)
        }

        if (localCustomerSheet != null) {
            //Show Local Data
            editTextCustomerName.setText(localCustomerSheet?.customerRepresentativeName)
            editTextCustomerNotes.setText(localCustomerSheet?.note)
            if (!localCustomerSheet?.signatureFilePath.isNullOrEmpty()) {
                showLocalSignatureOnUI(localCustomerSheet?.signatureFilePath)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            data?.let {
                val signatureFilePath = data.getStringExtra(AddSignatureActivity.ARG_SIGNATURE_FILE_PATH)
                showLocalSignatureOnUI(signatureFilePath)
                onFragmentInteractionListener?.isDataSave(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveLocalDataInState()
    }

    fun updateCustomerDetails(customerSheet: CustomerSheetData?, selectedTime: Long, inCompleteWorkItemsCount: Int) {
        this.customerSheet = customerSheet
        this.selectedTime = selectedTime
        this.inCompleteWorkItemsCount = inCompleteWorkItemsCount

        val isCurrentDate = DateUtils.isCurrentDate(selectedTime)

        customerSheet?.also {
            editTextCustomerName.setText(customerSheet.customerRepresentativeName)
            editTextCustomerNotes.setText(customerSheet.note)
            customerId= customerSheet.id!!
            updateUIVisibility(ValueUtils.getDefaultOrValue(customerSheet.isSigned), isCurrentDate, inCompleteWorkItemsCount, customerSheet.signatureUrl)
            onFragmentInteractionListener?.isDataSave(true)
        } ?: run {
            editTextCustomerName.setText("")
            editTextCustomerNotes.setText("")
            updateUIVisibility(false, isCurrentDate, inCompleteWorkItemsCount)
        }
    }

    private fun saveLocalDataInState() {
        onFragmentInteractionListener?.saveSateCustomerSheet(editTextCustomerName.text.toString(), editTextCustomerNotes.text.toString(), signatureFilePath)
    }

    private fun updateUIVisibility(signed: Boolean, currentDate: Boolean, inCompleteWorkItemsCount: Int, signatureUrl: String? = "") {
        buttonSubmit.visibility = if (currentDate) View.VISIBLE else View.GONE
        textViewSignature.visibility = View.GONE

        if (signed) {
            imageViewSignature.visibility = View.VISIBLE
            Glide.with(fragmentActivity!!).load(signatureUrl).into(imageViewSignature)
        } else {
            imageViewSignature.visibility = View.GONE
            Glide.with(fragmentActivity!!).clear(imageViewSignature)
        }

        if (!currentDate || signed /*|| inCompleteWorkItemsCount > 0*/) {
            editTextCustomerName.isEnabled = false
            editTextCustomerNotes.isEnabled = false
            buttonSubmit.isEnabled = false
            if (signed) {
                buttonSubmit.text = getText(R.string.sheet_submitted)
            } else {
                buttonSubmit.text = getText(R.string.submit)
            }
        } else {
            editTextCustomerName.isEnabled = true
            editTextCustomerNotes.isEnabled = true
            buttonSubmit.isEnabled = true
        }

        if (!signed && currentDate && inCompleteWorkItemsCount == 0) {
            textViewAddSignature.visibility = View.VISIBLE
        } else {
            textViewAddSignature.visibility = View.GONE
        }

        if (signed || (currentDate && inCompleteWorkItemsCount == 0)) {
            layoutSignature.visibility = View.VISIBLE
        } else {
            layoutSignature.visibility = View.GONE
        }
    }

    private fun showLocalSignatureOnUI(signatureFilePath: String?) {
        if (!signatureFilePath.isNullOrEmpty()) {
            this.signatureFilePath = signatureFilePath
            Glide.with(fragmentActivity!!).load(File(signatureFilePath)).into(imageViewSignature)
            imageViewSignature.visibility = View.VISIBLE
            textViewAddSignature.visibility = View.GONE
        } else {
            this.signatureFilePath = ""
            imageViewSignature.visibility = View.GONE
            textViewAddSignature.visibility = View.VISIBLE
        }
    }

    private fun submitCustomerSheet() {
        val customerName = editTextCustomerName.text.toString()
        val notesCustomer = editTextCustomerNotes.text.toString()
        if (customerName.isNotEmpty()  && (signatureFilePath.isNotEmpty()|| inCompleteWorkItemsCount > 0)) {
            var message = getString(R.string.submit_customer_sheet_alert_message)
            if (notesCustomer.isEmpty()) {
                message = getString(R.string.submit_customer_sheet_permanently_alert_message)
            }
            showConfirmationDialog(message, customerName, notesCustomer)
        } else {
            CustomProgressBar.getInstance().showErrorDialog(getString(R.string.customer_sheet_warning_message), fragmentActivity!!)
        }
    }

    private fun showConfirmationDialog(message: String, customerName: String, notesCustomer: String) {
        CustomProgressBar.getInstance().showWarningDialog(message, fragmentActivity!!, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                localCustomerSheet = null
                onFragmentInteractionListener?.saveCustomerSheet(customerName, notesCustomer, signatureFilePath, customerId)
            }

            override fun onCancelClick() {
            }
        })
    }

    override fun afterTextChanged(text: Editable?) {
        if (text === editTextCustomerName.editableText) {
            var nameCustomerBefore = ""
            var notesBefore = ""
            if (!customerSheet?.customerRepresentativeName.isNullOrEmpty() ) {
                nameCustomerBefore = customerSheet!!.customerRepresentativeName!!
                notesBefore = customerSheet!!.note!!
            }
            if (!nameCustomerBefore.equals(text.toString())|| !notesBefore.equals(editTextCustomerNotes.text.toString()))
                onFragmentInteractionListener!!.isDataSave(false)
            else onFragmentInteractionListener!!.isDataSave(true)

        } else if (text === editTextCustomerNotes.editableText) {
            var nameCustomerBefore = ""
            var notesBefore = ""
            if (!customerSheet?.note.isNullOrEmpty()) {
                nameCustomerBefore = customerSheet!!.customerRepresentativeName!!
                notesBefore =customerSheet!!.note!!
            }
            if (!notesBefore.equals(text.toString()) || !nameCustomerBefore.equals(editTextCustomerName.text.toString()))
                onFragmentInteractionListener!!.isDataSave(false)
            else onFragmentInteractionListener!!.isDataSave(true)

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> startIntent(AddSignatureActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                buttonSubmit.id -> submitCustomerSheet()
            }
        }
    }
}