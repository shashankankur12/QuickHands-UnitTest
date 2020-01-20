package com.quickhandslogistics.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.adapter.lumperJobDetailAdapter
import kotlinx.android.synthetic.main.activity_lumper_job_history.*
import kotlinx.android.synthetic.main.layout_header.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.DatePickerDialog
import io.bloco.faker.Faker
import java.util.*
import kotlin.collections.ArrayList


class LumperJobHistoryActivity : AppCompatActivity() {

    val lumperJobList: ArrayList<String> = ArrayList()
    var faker = Faker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_job_history)

        text_title?.text = faker.name.firstName()+" "+faker.name.lastName()

        lumperData()

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        recycler_lumper_job_history.layoutManager = LinearLayoutManager(this)
        recycler_lumper_job_history.adapter = this?.let { lumperJobDetailAdapter(lumperJobList, it) }

        linear_root.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@LumperJobHistoryActivity,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    text_filter_by_date.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })
    }

    fun lumperData() {
        lumperJobList.add("Nigel")
        lumperJobList.add("Mason")
        lumperJobList.add("Brent")
        lumperJobList.add("Denton")
        lumperJobList.add("Herman")
        lumperJobList.add("Cody")
        lumperJobList.add("Griffin")
        lumperJobList.add("Fletcher")
        lumperJobList.add("Leroy")
    }
}
