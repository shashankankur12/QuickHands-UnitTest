package com.quickhandslogistics.views.qhlContact

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.qhlContact.QhlContactAdapter
import com.quickhandslogistics.contracts.qhlContact.QhlContactContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.qhlContact.QhlOfficeInfo
import com.quickhandslogistics.presenters.qhlContact.QhlContactPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.ScheduleUtils.sortEmployeesList
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.fragment_qhl_contact.*

class QhlContactFragment : BaseFragment(), QhlContactContract.View, View.OnClickListener, QhlContactContract.View.OnAdapterItemClickListener {
    private lateinit var qhlContactPresenter: QhlContactPresenter
    private lateinit var qhlContactAdapter: QhlContactAdapter
    private var leadProfileData: QhlOfficeInfo? = null
    private var qhlContactList: ArrayList<EmployeeData> = ArrayList()
    private var phone: String? = null
    private var email: String = ""
    private var isShowErrorDialog: Boolean = true

    companion object {
        const val QHL_CONTACT_LIST = "QHL_CONTACT_LIST"
        const val HEADER_INFO = "HEADER_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qhlContactPresenter = QhlContactPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qhl_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewQhlContact.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            qhlContactAdapter = QhlContactAdapter(resources, this@QhlContactFragment)
            adapter = qhlContactAdapter
        }

        qhlContactAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(QHL_CONTACT_LIST)) {
                qhlContactList =
                    savedInstanceState.getSerializable(QHL_CONTACT_LIST) as ArrayList<EmployeeData>
                qhlContactList(qhlContactList)
            }

            if (savedInstanceState.containsKey(HEADER_INFO)) {
                leadProfileData = savedInstanceState.getParcelable(HEADER_INFO)
                showQhlHeaderInfo(leadProfileData)
            }


        } ?: run {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            qhlContactPresenter.fetchQhlContactList()
            isShowErrorDialog = true
        }
        textViewQHLContact.setOnClickListener(this)
        textViewQHlEmail.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (leadProfileData != null)
            outState.putParcelable(HEADER_INFO, leadProfileData)

        outState.putSerializable(QHL_CONTACT_LIST, qhlContactList)
        super.onSaveInstanceState(outState)
    }

    private fun invalidateEmptyView() {
        if (qhlContactAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            textViewEmptyData.text = getString(R.string.empty_contact_list_message)
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_contact_list_message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        qhlContactPresenter.onDestroy()
    }

    override fun showQhlHeaderInfo(leadProfileData: QhlOfficeInfo?) {
        this.leadProfileData = leadProfileData
        leadProfileData?.let {
            phone = it.phone?.replace("+1", "")?.replace("-", "")?.trim()
            val open = if (!it.opens.isNullOrEmpty()) DateUtils.changeUTCDateStringToLocalDateString(DateUtils.PATTERN_API_RESPONSE, DateUtils.PATTERN_TIME,
                it.opens!!.replace("\"","")
            ) else getString(R.string.na)
            val close = if (!it.closes.isNullOrEmpty())DateUtils.changeUTCDateStringToLocalDateString(DateUtils.PATTERN_API_RESPONSE, DateUtils.PATTERN_TIME,
                it.closes!!.replace("\"","")
            ) else getString(R.string.na)
            email = leadProfileData.email!!

            textViewQhlOfficeName.text = getString(R.string.qhl_office)
            textViewQhlAddress.text =
                if (!it.address.isNullOrEmpty()) it.address?.capitalize() else getString(R.string.na)
            textViewQhlOfficeTime.text = String.format(getString(R.string.hours), open, close)
            textViewQHlEmail.text =
                if (!it.email.isNullOrEmpty()) it.email else getString(R.string.na)
            textViewQHLContact.text =
                if (!phone.isNullOrEmpty()) UIUtils.formetMobileNumber(phone!!) else getString(R.string.na)
        }
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewQhlContact.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE

        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            if (isShowErrorDialog)
            CustomProgressBar.getInstance().showValidationErrorDialog(message, fragmentActivity!!)
            isShowErrorDialog =false
        } else SnackBarFactory.createSnackBar(fragmentActivity!!, mainRootLayout, message)
    }

    override fun qhlContactList(qhlContactList: ArrayList<EmployeeData>) {
        this.qhlContactList = qhlContactList

        val qhlMangerList: ArrayList<EmployeeData> = ArrayList()
        val qhlLeadList: ArrayList<EmployeeData> = ArrayList()
        val sortedList: ArrayList<EmployeeData> = ArrayList()

        qhlContactList.forEach {
            if (it.role?.equals(AppConstant.LEADS)!!) {
                qhlLeadList.add(it)
            } else if (it.role?.equals(AppConstant.DISTRICT_MANAGER)!!) {
                qhlMangerList.add(it)
            }
        }

        sortEmployeesList(qhlMangerList)
        sortEmployeesList(qhlLeadList)
        sortedList.addAll(qhlMangerList)
        sortedList.addAll(qhlLeadList)

        qhlContactAdapter.updateLumpersData(sortedList)
    }

    override fun showLoginScreen() {
        startIntent(
            LoginActivity::class.java,
            isFinish = true,
            flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun showSuccessMessageSend(message: String) {
        CustomProgressBar.getInstance().showSuccessDialog(message,
            fragmentActivity!!, object : CustomDialogListener {
                override fun onConfirmClick() {
                    qhlContactPresenter.fetchQhlContactList()
                    isShowErrorDialog = true
                }
            })
    }

    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                textViewQHLContact.id -> {
                    if (!phone.isNullOrEmpty())
                        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                }
                textViewQHlEmail.id -> {
                    if (email.isNotEmpty()) {
                        val emailIntent =
                            Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                        startActivity(Intent.createChooser(emailIntent, "Send email..."))
                    }
                }
            }
        }
    }

    override fun onItemClick(employeeData: EmployeeData) {}

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }


        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
    }

    override fun onEmailViewClick(lumperName: String, email: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    override fun onChatViewClick(employeeData: EmployeeData) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        employeeData.id?.let { id ->
            activity?.let {
                CustomBottomSheetDialog.sendMessageBottomSheetDialog(
                    it, object : CustomBottomSheetDialog.IDialogRequestCorrectionClick {
                        override fun onSendRequest(dialog: Dialog, request: String) {
                            dialog.dismiss()
                            qhlContactPresenter.sendCustomerContactMessage(id, request)
                            isShowErrorDialog = true

                        }
                    })
            }
        }


    }

}