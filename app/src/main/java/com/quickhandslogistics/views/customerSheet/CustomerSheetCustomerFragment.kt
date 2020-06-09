package com.quickhandslogistics.views.customerSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.common.AddSignatureActivity
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.fragment_customer_sheet_customer.*
import java.io.File

class CustomerSheetCustomerFragment : BaseFragment(), View.OnClickListener {

    private var onFragmentInteractionListener: CustomerSheetContract.View.OnFragmentInteractionListener? = null

    private var signatureFilePath = ""

    companion object {
        @JvmStatic
        fun newInstance() = CustomerSheetCustomerFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is CustomerSheetContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = parentFragment as CustomerSheetContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addNotesTouchListener(editTextCustomerNotes)

        textViewAddSignature.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            data?.let {
                val signatureFilePath = data.getStringExtra(AddSignatureActivity.ARG_SIGNATURE_FILE_PATH)
                showLocalSignatureOnUI(signatureFilePath)
            }
        }
    }

    fun updateCustomerDetails(customerSheet: CustomerSheetData?, selectedTime: Long, inCompleteWorkItemsCount: Int) {
        val isCurrentDate = DateUtils.isCurrentDate(selectedTime)

        customerSheet?.also {
            editTextCustomerName.setText(customerSheet.customerRepresentativeName)
            editTextCustomerNotes.setText(customerSheet.note)
            updateUIVisibility(ValueUtils.getDefaultOrValue(customerSheet.isSigned), isCurrentDate, inCompleteWorkItemsCount)
        } ?: run {
            editTextCustomerName.setText("")
            editTextCustomerNotes.setText("")
            updateUIVisibility(false, isCurrentDate, inCompleteWorkItemsCount)
        }
    }

    private fun updateUIVisibility(signed: Boolean, currentDate: Boolean, inCompleteWorkItemsCount: Int) {
        imageViewSignature.visibility = View.GONE
        buttonSubmit.visibility = if (currentDate) View.VISIBLE else View.GONE
        textViewSignature.visibility = if (signed) View.VISIBLE else View.GONE

        if (!currentDate || signed || inCompleteWorkItemsCount > 0) {
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
        if (customerName.isNotEmpty() && signatureFilePath.isNotEmpty()) {
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
                onFragmentInteractionListener?.saveCustomerSheet(customerName, notesCustomer, signatureFilePath)
            }

            override fun onCancelClick() {
            }
        })
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