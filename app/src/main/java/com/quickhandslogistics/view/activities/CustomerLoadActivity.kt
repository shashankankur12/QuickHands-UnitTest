package com.quickhandslogistics.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.DialogHelper
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.adapter.CustomerJobDetailAdapter
import com.quickhandslogistics.view.adapter.lumperJobDetailAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_customer_load.*
import kotlinx.android.synthetic.main.activity_lumper_sheet_detail.*
import kotlinx.android.synthetic.main.layout_header.*

class CustomerLoadActivity : AppCompatActivity() {

    var lumperJobDetail: String = ""
    val lumperSheetList: ArrayList<String> = ArrayList()
    var faker = Faker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_load)

        text_title?.text = faker?.company?.name()

        lumperSheetData()

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        image_notes.visibility = View.VISIBLE
        image_signature.visibility = View.VISIBLE

        recycler_customer_sheet_history.layoutManager = LinearLayoutManager(this)
        recycler_customer_sheet_history.adapter = this?.let { CustomerJobDetailAdapter(lumperSheetList, it) }

        fab_add_customer_sheet.setOnClickListener { view ->
            val intent = Intent(this, CustomerChooseActivity::class.java)
            startActivity(intent)
        }

        image_notes.setOnClickListener(View.OnClickListener { view ->

            DialogHelper.showNotesDialog(R.style.dialogAnimation, this)
        })

        image_signature.setOnClickListener(View.OnClickListener { view ->
            val intent = Intent(this, SignatureActivity::class.java)
            startActivity(intent)
        })
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
