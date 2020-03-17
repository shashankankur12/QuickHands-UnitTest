package com.quickhandslogistics.modified.views.activities

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.presenters.ChooseLumperPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.ChooseLumperAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.content_choose_lumper.*

class ChooseLumperActivity : BaseActivity(), ChooseLumperContract.View, TextWatcher,
    View.OnClickListener, ChooseLumperContract.View.OnAdapterItemClickListener {

    private lateinit var chooseLumperPresenter: ChooseLumperPresenter
    private lateinit var chooseLumperAdapter: ChooseLumperAdapter

    private var progressDialog: Dialog? = null

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
            chooseLumperAdapter = ChooseLumperAdapter(this@ChooseLumperActivity)
            adapter = chooseLumperAdapter
        }

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        chooseLumperPresenter = ChooseLumperPresenter(this, resources)
        chooseLumperPresenter.fetchLumpersList()
    }

    /*
    * Presenter Listeners
    */
    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(activity).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(lumperDataList: ArrayList<LumperData>) {
        chooseLumperAdapter.updateLumpersData(lumperDataList)
        if (lumperDataList.size > 0) {
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
    override fun onClickLumperDetail(lumperData: LumperData) {
        val bundle = Bundle()
        bundle.putSerializable(LumperDetailActivity.ARG_LUMPER_DATA, lumperData)
        startIntent(LumperDetailActivity::class.java, bundle = bundle)
    }

    override fun onSelectLumper(lumperData: LumperData) {
        onBackPressed()
    }
}
