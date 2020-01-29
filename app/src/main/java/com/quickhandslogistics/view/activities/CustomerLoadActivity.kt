package com.quickhandslogistics.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.adapter.lumperJobDetailAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_lumper_sheet_detail.*
import kotlinx.android.synthetic.main.layout_header.*

class CustomerLoadActivity : AppCompatActivity() {

    val lumperSheetList: ArrayList<String> = ArrayList()
    var faker = Faker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_sheet_detail)

        text_title?.text = faker?.company?.name()

        lumperSheetData()

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        recycler_lumper_sheet_history.layoutManager = LinearLayoutManager(this)
        recycler_lumper_sheet_history.adapter = this?.let { lumperJobDetailAdapter(lumperSheetList, it) }
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
}
