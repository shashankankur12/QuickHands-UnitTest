package com.quickhandslogistics.modified.views.customerSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.common.AddSignatureActivity
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.fragment_customer_sheet_customer.*
import java.io.File

class CustomerSheetCustomerFragment : BaseFragment(), View.OnClickListener {

    private var onFragmentInteractionListener: CustomerSheetContract.View.OnFragmentInteractionListener? =
        null

    private var signatureFilePath = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is CustomerSheetContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener =
                parentFragment as CustomerSheetContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewAddSignature.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> {
                    startIntent(
                        AddSignatureActivity::class.java,
                        requestCode = AppConstant.REQUEST_CODE_CHANGED
                    )
                }
                buttonSubmit.id -> {
                    val customerName = editTextCustomerName.text.toString()
                    val notesCustomer = editTextCustomerNotes.text.toString()
                    if (customerName.isNotEmpty() && signatureFilePath.isNotEmpty()) {
                        var message = getString(R.string.string_ask_to_submit_customer_sheet)
                        if (notesCustomer.isEmpty()) {
                            message =
                                getString(R.string.string_ask_to_submit_customer_sheet_permanently)
                        }
                        CustomProgressBar.getInstance().showWarningDialog(
                            message, fragmentActivity!!, object : CustomDialogWarningListener {
                                override fun onConfirmClick() {
                                    onFragmentInteractionListener?.saveCustomerSheet(
                                        customerName, notesCustomer, signatureFilePath
                                    )
                                }

                                override fun onCancelClick() {
                                }
                            })

                    } else {
                        CustomProgressBar.getInstance().showErrorDialog(
                            getString(R.string.customer_sheet_warning_message), fragmentActivity!!
                        )
                    }
                }
            }
        }
    }

    fun updateCustomerDetails(
        customerSheet: CustomerSheetListAPIResponse.CustomerSheetData?,
        selectedTime: Long, inCompleteWorkItemsCount: Int
    ) {
        val isCurrentDate = DateUtils.isCurrentDate(selectedTime)
        customerSheet?.also {
            editTextCustomerName.setText(customerSheet.customerRepresentativeName)
            editTextCustomerNotes.setText(customerSheet.note)

            updateUIVisibility(
                ValueUtils.getDefaultOrValue(customerSheet.isSigned),
                isCurrentDate, inCompleteWorkItemsCount
            )
        } ?: run {
            updateUIVisibility(false, isCurrentDate, inCompleteWorkItemsCount)
        }
    }

    private fun updateUIVisibility(
        signed: Boolean, currentDate: Boolean, inCompleteWorkItemsCount: Int
    ) {
        if (!currentDate || signed || inCompleteWorkItemsCount > 0) {
            editTextCustomerName.isEnabled = false
            editTextCustomerNotes.isEnabled = false
            textViewAddSignature.visibility = View.GONE
            textViewSignature.visibility = if (signed) View.VISIBLE else View.GONE
            buttonSubmit.visibility = View.GONE
        } else {
            editTextCustomerName.isEnabled = true
            editTextCustomerNotes.isEnabled = true
            textViewAddSignature.visibility = if (signed) View.GONE else View.VISIBLE
            textViewSignature.visibility = if (signed) View.VISIBLE else View.GONE
            buttonSubmit.visibility = View.VISIBLE
        }
        imageViewSignature.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            data?.let {
                val signatureFilePath =
                    data.getStringExtra(AddSignatureActivity.ARG_SIGNATURE_FILE_PATH)
                showLocalSignatureOnUI(signatureFilePath)
            }
        }
    }

    private fun showLocalSignatureOnUI(signatureFilePath: String?) {
        if (!signatureFilePath.isNullOrEmpty()) {
            this.signatureFilePath = signatureFilePath
            Glide.with(fragmentActivity!!).load(File(signatureFilePath))
                .into(imageViewSignature)
            imageViewSignature.visibility = View.VISIBLE
            textViewAddSignature.visibility = View.GONE
        } else {
            this.signatureFilePath = ""
            imageViewSignature.visibility = View.GONE
            textViewAddSignature.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CustomerSheetCustomerFragment()
    }
}