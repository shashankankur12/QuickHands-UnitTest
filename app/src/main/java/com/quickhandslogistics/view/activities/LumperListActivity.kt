package com.quickhandslogistics.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.LumperModel
import com.quickhandslogistics.view.adapter.LumperAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.fragment_lumper.*
import kotlinx.android.synthetic.main.layout_header.*

class LumperListActivity : AppCompatActivity() {
    val lumperList: ArrayList<LumperModel> = ArrayList()
    var lumperJobDetail: String = ""
    lateinit var lumperAdapter: LumperAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_list)
        if(intent.hasExtra(getString(R.string.string_lumper)))
            lumperJobDetail = intent.getStringExtra(getString(R.string.string_lumper))
            text_title?.setText(getString(R.string.choose_lumper))

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        recycler_lumper.layoutManager = LinearLayoutManager(this@LumperListActivity)

        val faker = Faker()
        for (i in 1..20) {
            lumperList.add(LumperModel(faker.name.firstName(), faker.name.lastName()))
        }

        lumperAdapter =  LumperAdapter(lumperList, this@LumperListActivity!!,lumperJobDetail)
        recycler_lumper.adapter = this@LumperListActivity?.let { lumperAdapter}
    }
}
