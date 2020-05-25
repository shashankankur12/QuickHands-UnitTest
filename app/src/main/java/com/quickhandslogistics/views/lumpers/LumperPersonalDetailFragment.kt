package com.quickhandslogistics.views.lumpers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.fragment_lumper_personal_detail.*

class LumperPersonalDetailFragment : BaseFragment(), View.OnClickListener {

    private var employeeData: EmployeeData? = null

    companion object {
        @JvmStatic
        fun newInstance(employeeData: EmployeeData) = LumperPersonalDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_LUMPER_DATA, employeeData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lumper_personal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeData?.let {
            textViewFirstName.text = if (!it.firstName.isNullOrEmpty()) it.firstName else "-"
            textViewLastName.text = if (!it.lastName.isNullOrEmpty()) it.lastName else "-"
            textViewEmployeeId.text = if (!it.employeeId.isNullOrEmpty()) it.employeeId else "-"
            textViewEmailAddress.text = if (!it.email.isNullOrEmpty()) it.email else "-"

            val phoneNumber = UIUtils.getDisplayPhoneNumber(it)
            textViewPhoneNumber.text = if (phoneNumber.isNotEmpty()) phoneNumber else "-"

            layoutPhoneNumber.setOnClickListener(this@LumperPersonalDetailFragment)
            layoutEmailAddress.setOnClickListener(this@LumperPersonalDetailFragment)
        }
    }

    private fun showPhoneNumberDialog() {
        val name = UIUtils.getEmployeeFullName(employeeData)
        employeeData?.phone?.let { phone ->
            CustomProgressBar.getInstance().showWarningDialog(String.format(getString(R.string.call_lumper_alert_message), name),
                fragmentActivity!!, object : CustomDialogWarningListener {
                    override fun onConfirmClick() {
                        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                    }

                    override fun onCancelClick() {
                    }
                })
        }
    }

    private fun showEmailDialog() {
        val name = UIUtils.getEmployeeFullName(employeeData)
        employeeData?.email?.let { email ->
            CustomProgressBar.getInstance().showWarningDialog(String.format(getString(R.string.email_lumper_alert_message), name),
                fragmentActivity!!, object : CustomDialogWarningListener {
                    override fun onConfirmClick() {
                        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                        startActivity(Intent.createChooser(emailIntent, "Send email..."))
                    }

                    override fun onCancelClick() {
                    }
                })
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                layoutPhoneNumber.id -> {
                    showPhoneNumberDialog()
                }
                layoutEmailAddress.id -> {
                    showEmailDialog()
                }
            }
        }
    }
}