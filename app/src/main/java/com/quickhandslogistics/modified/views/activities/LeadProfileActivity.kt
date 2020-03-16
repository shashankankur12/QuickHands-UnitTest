package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity

class LeadProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_profile)
        setupToolbar(title = getString(R.string.my_profile))
    }
}
