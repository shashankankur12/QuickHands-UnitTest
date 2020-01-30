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

class LumperSheetDetailActivity : AppCompatActivity() {

    var lumperJobDetail: String = ""
    val lumperSheetList: ArrayList<String> = ArrayList()
    var faker = Faker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_sheet_detail)

        text_title?.text = faker.name.firstName()+" "+faker.name.lastName()

        lumperSheetData()

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        if (intent.hasExtra(getString(R.string.string_lumper_sheet_status))) {
            lumperJobDetail = intent.getStringExtra(getString(R.string.string_lumper_sheet_status))
            if (lumperJobDetail.equals(getString(R.string.complete))) {
                fab_add_lumper.visibility = View.GONE
            } else if (lumperJobDetail.equals(getString(R.string.in_progress))) {
                fab_add_lumper.visibility = View.VISIBLE
            }
        }

        fab_add_lumper.setOnClickListener {
            val intent = Intent(this@LumperSheetDetailActivity, CompleteLumperJobHistoryDetails::class.java)
            intent.putExtra(this@LumperSheetDetailActivity.getString(R.string.string_lumper_sheet),this@LumperSheetDetailActivity.getString(R.string.string_lumper_sheet) )
            this@LumperSheetDetailActivity.startActivity(intent)
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
