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
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.qhlContact.QhlContactPresenter
import com.quickhandslogistics.utils.ConnectionDetector
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.fragment_customer_contect.*
import kotlinx.android.synthetic.main.fragment_customer_contect.textViewEmptyData
import kotlinx.android.synthetic.main.fragment_lumpers.*

class QhlContactFragment : BaseFragment(), QhlContactContract.View, View.OnClickListener,
    QhlContactContract.View.OnAdapterItemClickListener {

    private lateinit var qhlContactPresenter: QhlContactPresenter
    private lateinit var qhlContactAdapter: QhlContactAdapter

    companion object {
        const val CUSTOMER_DETAIL_LIST = "CUSTOMER_DETAIL_LIST"
        const val DATE_SELECTED = "DATE_SELECTED"
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

        customerListContact.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            qhlContactAdapter = QhlContactAdapter(this@QhlContactFragment)
            adapter = qhlContactAdapter
        }

        qhlContactAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })
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

    override fun showDateString(dateString: String) {

    }

    override fun showAPIErrorMessage(message: String) {
        customerListContact.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {

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