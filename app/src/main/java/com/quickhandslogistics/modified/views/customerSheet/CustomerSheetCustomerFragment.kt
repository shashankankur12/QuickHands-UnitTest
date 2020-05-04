package com.quickhandslogistics.modified.views.customerSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.common.AddSignatureActivity
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_customer_sheet_customer.*

class CustomerSheetCustomerFragment : BaseFragment(), View.OnClickListener {

    private var onFragmentInteractionListener: CustomerSheetContract.View.OnFragmentInteractionListener? =
        null

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
                    startIntent(AddSignatureActivity::class.java)
                }
                buttonSubmit.id -> {
                    val notesQHLCustomer = editTextQHLCustomerNotes.text.toString()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CustomerSheetCustomerFragment()
    }
}