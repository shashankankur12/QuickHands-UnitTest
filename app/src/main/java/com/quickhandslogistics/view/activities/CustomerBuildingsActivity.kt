package com.quickhandslogistics.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.CustomerBuildingsAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_customer_buildings.*
import kotlinx.android.synthetic.main.layout_header.*

class CustomerBuildingsActivity : AppCompatActivity() {

    val faker = Faker()
    private lateinit var customerBuildingsAdapter: CustomerBuildingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_buildings)

        text_title.text = getString(R.string.string_building)

        image_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }

        getIntentData()

        setUpAdapter()
    }

    private fun getIntentData() {
        if (intent != null) {
            text_title.text = intent.getStringExtra("name") ?: "One97 Communications"
        }
    }

    private fun setUpAdapter() {

        var linearLayoutManager = LinearLayoutManager(this)
        recycler_customer_building.layoutManager = linearLayoutManager

        customerBuildingsAdapter = CustomerBuildingsAdapter(this)
        recycler_customer_building.adapter = customerBuildingsAdapter
        recycler_customer_building.scheduleLayoutAnimation()
    }
}
