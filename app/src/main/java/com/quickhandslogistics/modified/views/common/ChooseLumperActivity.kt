package com.quickhandslogistics.modified.views.common

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.common.ChooseLumperAdapter
import com.quickhandslogistics.modified.contracts.common.ChooseLumperContract
import com.quickhandslogistics.modified.contracts.common.InfoDialogWarningContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.common.ChooseLumperPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.content_choose_lumper.*

class ChooseLumperActivity : BaseActivity(), ChooseLumperContract.View, TextWatcher,
    View.OnClickListener, ChooseLumperContract.View.OnAdapterItemClickListener {

    private lateinit var chooseLumperPresenter: ChooseLumperPresenter
    private lateinit var chooseLumperAdapter: ChooseLumperAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_lumper)
        setupToolbar(title = getString(R.string.choose_lumper))

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            chooseLumperAdapter =
                ChooseLumperAdapter(
                    this@ChooseLumperActivity
                )
            adapter = chooseLumperAdapter
        }

        chooseLumperAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (chooseLumperAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        chooseLumperPresenter =
            ChooseLumperPresenter(
                this,
                resources
            )
        chooseLumperPresenter.fetchLumpersList()
    }

    /*
    * Presenter Listeners
    */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        chooseLumperAdapter.updateLumpersData(employeeDataList)
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
            chooseLumperAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(activity)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chooseLumperPresenter.onDestroy()
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onClickLumperDetail(employeeData: EmployeeData) {
        val bundle = Bundle()
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        startIntent(LumperDetailActivity::class.java, bundle = bundle)
    }

    override fun onSelectLumper(employeeData: EmployeeData) {
        val dialog = InfoWarningDialogFragment.newInstance(
            getString(R.string.string_ask_to_replace_new_lumper),
            positiveButtonText = getString(R.string.string_yes),
            negativeButtonText = getString(R.string.string_no),
            onClickListener = object : InfoDialogWarningContract.View.OnClickListener {
                override fun onPositiveButtonClick() {
                    onBackPressed()
                }

                override fun onNegativeButtonClick() {
                }
            })
        dialog.show(supportFragmentManager, InfoWarningDialogFragment::class.simpleName)
    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {

    }
}
