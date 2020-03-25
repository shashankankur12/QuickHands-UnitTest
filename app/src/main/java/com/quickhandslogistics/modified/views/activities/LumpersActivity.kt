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
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.LumperListAdapter
import com.quickhandslogistics.modified.views.fragments.InfoWarningDialogFragment
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_lumpers.*

class LumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    LumperListAdapter.OnAdapterItemClickListener{

    private lateinit var lumperListAdapter: LumperListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumpers)

        setupToolbar(title = getString(R.string.string_lumper))

        recyclerViewLumpersSheet.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumperListAdapter =
            LumperListAdapter(this@LumpersActivity)
            adapter = lumperListAdapter
        }

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
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

    override fun onItemClick() {
        startIntent(LumperDetailActivity::class.java)
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
