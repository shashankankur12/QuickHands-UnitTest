package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.ContainerDetailAdapter
import kotlinx.android.synthetic.main.activity_container_detail.*

class ContainerDetailActivity : BaseActivity(){
    private lateinit var containerDetailAdapter: ContainerDetailAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_detail)

        setupToolbar("Container Details")

        recyclerViewLumperDetail.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            containerDetailAdapter =
                ContainerDetailAdapter(activity)
            adapter = containerDetailAdapter
        }
    }
}
