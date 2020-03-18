package com.quickhandslogistics.modified.views.fragments.lumpers

import android.app.Dialog
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogContract
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.lumpers.LumpersPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.LumperDetailActivity
import com.quickhandslogistics.modified.views.adapters.LumpersAdapter
import com.quickhandslogistics.modified.views.fragments.InfoDialogFragment
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.fragment_lumpers.*

class LumpersFragment : BaseFragment(), LumpersContract.View, TextWatcher, View.OnClickListener,
    LumpersContract.View.OnAdapterItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var lumpersAdapter: LumpersAdapter
    private lateinit var lumpersPresenter: LumpersPresenter
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumpersPresenter = LumpersPresenter(this, resources)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lumpers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter = LumpersAdapter(this@LumpersFragment)
            adapter = lumpersAdapter
        }

        swipeRefreshLayoutLumpers.setColorSchemeColors(
            ContextCompat.getColor(
                fragmentActivity!!,
                R.color.colorAccent
            )
        )

        swipeRefreshLayoutLumpers.setOnRefreshListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        lumpersPresenter.fetchLumpersList()
    }

    /*
    * Presenter Listeners
    */
    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(fragmentActivity!!).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        lumpersAdapter.updateLumpersData(employeeDataList)
        if (employeeDataList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    /*
    * Native Views Listeners
    */
    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumpersAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(fragmentActivity!!)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lumpersPresenter.onDestroy()
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onItemClick(employeeData: EmployeeData) {
        val bundle = Bundle()
        bundle.putSerializable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        startIntent(LumperDetailActivity::class.java, bundle = bundle)
    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        val dialog = InfoDialogFragment.newInstance(
            String.format(getString(R.string.call_lumper_dialog_message), lumperName),
            onClickListener = object : InfoDialogContract.View.OnClickListener {
                override fun onPositiveButtonClick() {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                }
            })
        dialog.show(childFragmentManager, InfoDialogFragment::class.simpleName)
    }

    override fun onRefresh() {
        swipeRefreshLayoutLumpers.isRefreshing = false
        lumpersPresenter.fetchLumpersList()
    }
}
