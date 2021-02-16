package com.quickhandslogistics.views.qhlContact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.qhlContact.QhlContactAdapter
import com.quickhandslogistics.contracts.qhlContact.QhlContactContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.qhlContact.QhlContactPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.customerContact.CustomerContactFragment
import kotlinx.android.synthetic.main.fragment_qhl_contact.*

class QhlContactFragment : BaseFragment(), QhlContactContract.View, View.OnClickListener,
    QhlContactContract.View.OnAdapterItemClickListener {

    private lateinit var qhlContactPresenter: QhlContactPresenter
    private lateinit var qhlContactAdapter: QhlContactAdapter

    companion object {
        const val QHL_CONTACT_LIST = "QHL_CONTACT_LIST"
        const val HEADER_INFO = "HEADER_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qhlContactPresenter = QhlContactPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qhl_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewQhlContact.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
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

            }

            if (savedInstanceState.containsKey(HEADER_INFO)){

            }
            showQhlHeaderInfo(null)

        } ?: run {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            qhlContactPresenter.fetchQhlContactList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun invalidateEmptyView() {
        if (qhlContactAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            textViewEmptyData.text = getString(R.string.empty_lumper_list_message)
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumper_list_message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        qhlContactPresenter.onDestroy()
    }

    override fun showQhlHeaderInfo(leadProfileData: LeadProfileData?) {
        textViewQhlOfficeName.text= "QHL Office"
        textViewQhlAddress.text= "963 Fake St. Colton, CA 92316"
        textViewQhlOfficeTime.text= String.format(getString(R.string.hours), "8:00 AM","5:00 PM")
        textViewQhlOfficeTime.text= String.format(getString(R.string.hours), "8:00 AM","5:00 PM")
        textViewQHlEmail.text= "Operation@QHL.com"
        textViewQHLContact.text= UIUtils.formetMobileNumber("8090889709")
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewQhlContact.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainRootLayout, message)
    }

    override fun qhlContactList(employeeDataList: ArrayList<EmployeeData>) {

    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onClick(v: View?) {

    }

    override fun onItemClick(employeeData: EmployeeData) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        CustomProgressBar.getInstance().showWarningDialog(String.format(getString(R.string.call_lumper_alert_message), lumperName),
            fragmentActivity!!, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                }

                override fun onCancelClick() {
                }
            })

    }

}