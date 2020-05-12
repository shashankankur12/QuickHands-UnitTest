package com.quickhandslogistics.modified.views.lumpers

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
import com.quickhandslogistics.modified.adapters.lumpers.LumpersAdapter
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.lumpers.LumpersPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.fragment_lumpers.*

class LumpersFragment : BaseFragment(), LumpersContract.View, TextWatcher, View.OnClickListener,
    LumpersContract.View.OnAdapterItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var currentPageIndex: Int = 1
    private var nextPageIndex: Int = 1
    private var totalPagesCount: Int = 1

    private lateinit var lumpersAdapter: LumpersAdapter
    private lateinit var lumpersPresenter: LumpersPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumpersPresenter = LumpersPresenter(this, resources)
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
            addOnScrollListener(onScrollListener)
        }

        lumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (lumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        swipeRefreshLayoutLumpers.setColorSchemeColors(ContextCompat.getColor(fragmentActivity!!, R.color.buttonRed))

        swipeRefreshLayoutLumpers.setOnRefreshListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        lumpersPresenter.fetchLumpersList(currentPageIndex)
    }

    private fun resetPaginationValues() {
        currentPageIndex = 1
        nextPageIndex = 1
        totalPagesCount = 1
    }

    /*
    * Presenter Listeners
    */
    override fun showAPIErrorMessage(message: String) {
        recyclerViewLumpers.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int) {
        lumpersAdapter.updateLumpersData(employeeDataList, currentPageIndex)
        if (employeeDataList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }

        this.totalPagesCount = totalPagesCount
        this.nextPageIndex = nextPageIndex
    }

    /*
    * Native Views Listeners
    */
    private val onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.layoutManager?.let { layoutManager ->
                if (layoutManager is LinearLayoutManager) {
                    val visibleItemCount: Int = layoutManager.childCount
                    val totalItemCount: Int = layoutManager.itemCount
                    val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                    if (currentPageIndex != totalPagesCount) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPageIndex = nextPageIndex
                            lumpersPresenter.fetchLumpersList(currentPageIndex)
                        }
                    }
                }
            }
        }
    }

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

    override fun onRefresh() {
        swipeRefreshLayoutLumpers.isRefreshing = false
        resetPaginationValues()
        lumpersPresenter.fetchLumpersList(currentPageIndex)
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
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        startIntent(LumperDetailActivity::class.java, bundle = bundle)
    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        CustomProgressBar.getInstance().showWarningDialog(
            String.format(getString(R.string.call_lumper_dialog_message), lumperName),
            fragmentActivity!!, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                }

                override fun onCancelClick() {
                }
            })
    }
}
