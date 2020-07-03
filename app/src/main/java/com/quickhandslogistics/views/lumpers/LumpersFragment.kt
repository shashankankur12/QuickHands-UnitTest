package com.quickhandslogistics.views.lumpers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.lumpers.LumpersAdapter
import com.quickhandslogistics.contracts.lumpers.LumpersContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.lumpers.LumpersPresenter
import com.quickhandslogistics.utils.AppUtils
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_lumpers.*

class LumpersFragment : BaseFragment(), LumpersContract.View, TextWatcher, View.OnClickListener,
    LumpersContract.View.OnAdapterItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var employeeDataList: ArrayList<EmployeeData>? = null
    private lateinit var lumpersAdapter: LumpersAdapter
    private lateinit var lumpersPresenter: LumpersPresenter

    companion object {
        const val LUMPER_DETAIL_LIST = "LUMPER_DETAIL_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumpersPresenter = LumpersPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lumpers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter = LumpersAdapter(this@LumpersFragment)
            adapter = lumpersAdapter
        }

        lumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        swipeRefreshLayoutLumpers.setColorSchemeColors(ContextCompat.getColor(fragmentActivity!!, R.color.buttonRed))

        swipeRefreshLayoutLumpers.setOnRefreshListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LUMPER_DETAIL_LIST)) {
                employeeDataList = savedInstanceState.getParcelableArrayList(LUMPER_DETAIL_LIST)
                showLumpersData(employeeDataList!!)
            }
        } ?: run {
            lumpersPresenter.fetchLumpersList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lumpersPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (employeeDataList != null)
            outState.putParcelableArrayList(LUMPER_DETAIL_LIST, employeeDataList)
        super.onSaveInstanceState(outState)
    }

    private fun invalidateEmptyView() {
        if (lumpersAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (lumpersAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(fragmentActivity!!)
                }
            }
        }
    }

    override fun onRefresh() {
        swipeRefreshLayoutLumpers.isRefreshing = false
        lumpersPresenter.fetchLumpersList()
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumpersAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        recyclerViewLumpers.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.employeeDataList = employeeDataList
        lumpersAdapter.updateLumpersData(employeeDataList)
    }

    /** Adapter Listeners */
    override fun onItemClick(employeeData: EmployeeData) {
        val bundle = Bundle()
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        startIntent(LumperDetailActivity::class.java, bundle = bundle)
    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {
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
