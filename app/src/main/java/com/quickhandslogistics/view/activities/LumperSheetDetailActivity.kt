package com.quickhandslogistics.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.common.InfoDialogWarningContract
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.ContainerDetailActivity
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity
import com.quickhandslogistics.modified.views.common.InfoWarningDialogFragment
import com.quickhandslogistics.view.adapter.lumperJobDetailAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_lumper_sheet_detail.*

class LumperSheetDetailActivity : BaseActivity(), View.OnClickListener, lumperJobDetailAdapter.OnAdapterItemClickListener {

    var lumperJobDetail: String = ""
    val lumperSheetList: ArrayList<String> = ArrayList()
    var faker = Faker()
    private lateinit var lumpersAdapter: lumperJobDetailAdapter

    companion object {
        const val ARG_LUMPER_SHEET_DATA = "ARG_LUMPER_SHEET_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_sheet_detail)

        setupToolbar()

        lumperSheetData()

        if (intent.hasExtra(getString(R.string.string_lumper_sheet_status))) {
            lumperJobDetail = intent.getStringExtra(getString(R.string.string_lumper_sheet_status))

        }

        recycler_lumper_sheet_history.apply {
            val linearLayoutManager = LinearLayoutManager(activity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter = lumperJobDetailAdapter(lumperSheetList, this@LumperSheetDetailActivity)
            adapter = lumpersAdapter
        }

        imageViewCall.setOnClickListener(this)
        lumperDetail.setOnClickListener(this)
    }

    fun lumperSheetData() {
        lumperSheetList.add("Nigel")
        lumperSheetList.add("Mason")
        lumperSheetList.add("Brent")
        lumperSheetList.add("Denton")
        lumperSheetList.add("Herman")
        lumperSheetList.add("Cody")
        lumperSheetList.add("Griffin")
        lumperSheetList.add("Fletcher")
        lumperSheetList.add("Leroy")
    }

     fun onPhoneViewClick(lumperName: String, phone: String) {
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

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCall.id -> {
                    onPhoneViewClick("John Snow ", "34565839284")
                }

                lumperDetail.id -> {
                    startIntent(LumperDetailActivity::class.java)
                }
            }
        }
    }

    override fun onItemClick() {
        startIntent(ContainerDetailActivity::class.java)
    }
}
