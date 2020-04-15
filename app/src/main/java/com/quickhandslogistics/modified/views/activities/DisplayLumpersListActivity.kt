package com.quickhandslogistics.modified.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogWarningContract
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.LumperListAdapter
import com.quickhandslogistics.modified.views.fragments.InfoWarningDialogFragment
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.content_choose_lumper.*

class DisplayLumpersListActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    LumpersContract.View.OnAdapterItemClickListener {

    private lateinit var lumperListAdapter: LumperListAdapter

    companion object {
        const val ARG_LUMPERS_LIST = "ARG_LUMPERS_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lumpers_list)
        setupToolbar(title = getString(R.string.string_lumper))

        intent.extras?.let {
            val lumpersList = it.getParcelableArrayList<EmployeeData>(ARG_LUMPERS_LIST)

            lumpersList?.let {
                lumpersList.sortWith(Comparator { lumper1, lumper2 ->
                    if (!StringUtils.isNullOrEmpty(lumper1.firstName)
                        && !StringUtils.isNullOrEmpty(lumper2.firstName)
                    ) {
                        lumper1.firstName?.toLowerCase()!!
                            .compareTo(lumper2.firstName?.toLowerCase()!!)
                    } else {
                        0
                    }
                })

                recyclerViewLumpers.apply {
                    val linearLayoutManager = LinearLayoutManager(activity)
                    layoutManager = linearLayoutManager
                    val dividerItemDecoration =
                        DividerItemDecoration(activity, linearLayoutManager.orientation)
                    addItemDecoration(dividerItemDecoration)
                    lumperListAdapter =
                        LumperListAdapter(lumpersList, this@DisplayLumpersListActivity)
                    adapter = lumperListAdapter
                }

                editTextSearch.addTextChangedListener(this)
                imageViewCancel.setOnClickListener(this)
            }
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

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        text?.let {
            lumperListAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onItemClick(employeeData: EmployeeData) {
        val bundle = Bundle()
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        startIntent(LumperDetailActivity::class.java, bundle = bundle)
    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        val dialog = InfoWarningDialogFragment.newInstance(
            String.format(getString(R.string.call_lumper_dialog_message), lumperName),
            onClickListener = object : InfoDialogWarningContract.View.OnClickListener {
                override fun onPositiveButtonClick() {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                }

                override fun onNegativeButtonClick() {
                }
            })
        dialog.show(supportFragmentManager, InfoWarningDialogFragment::class.simpleName)
    }
}
