package com.quickhandslogistics.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.LumperJobHistoryAdapter
import com.quickhandslogistics.modified.contracts.common.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.common.ChooseLumperPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.content_choose_lumper.*

class LumperJobHistoryActivity : BaseActivity(), ChooseLumperContract.View, TextWatcher,
    View.OnClickListener, ChooseLumperContract.View.OnAdapterItemClickListener {

    private lateinit var chooseLumperAdapter: LumperJobHistoryAdapter
    private lateinit var chooseLumperPresenter: ChooseLumperPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_job_history2)

        setupToolbar(getString(R.string.lumper_job_history))

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            chooseLumperAdapter =
                LumperJobHistoryAdapter(this@LumperJobHistoryActivity)
            adapter = chooseLumperAdapter
        }

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        chooseLumperPresenter =
            ChooseLumperPresenter(
                this,
                resources
            )
        chooseLumperPresenter.fetchLumpersList()
    }

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        CustomProgressBar.getInstance().showWarningDialog(
            String.format(getString(R.string.call_lumper_dialog_message), lumperName),
            activity, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                }

                override fun onCancelClick() {
                }
            })
    }
}