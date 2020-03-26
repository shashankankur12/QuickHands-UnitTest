package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity

class ContainerDetailActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_detail)

        setupToolbar("Container Details")
    }
}
