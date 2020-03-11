package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.MenuItem
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.views.BaseActivity
import kotlinx.android.synthetic.main.content_lumper_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class LumperDetailActivity : BaseActivity() {

    private var lumperData: LumperData? = null

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_detail)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        textViewTitle.text = getString(R.string.string_lumper_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        displayLumperDetails()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayLumperDetails() {
        intent.extras?.let {
            if (it.containsKey(ARG_LUMPER_DATA)) {
                lumperData = it.getSerializable(ARG_LUMPER_DATA) as LumperData

                lumperData?.let {
                    editTextLumperName.setText(
                        String.format(
                            "%s %s",
                            lumperData?.firstName,
                            lumperData?.lastName
                        )
                    )
                    editTextEmail.setText(lumperData?.email)
                    editTextPhone.setText(lumperData?.phone)
                    editTextRole.setText(lumperData?.role)
                    editTextCreatedAt.setText(lumperData?.created_at)
                    editTextUpdatedAt.setText(lumperData?.updated_at)
                }
            }
        }
    }
}
