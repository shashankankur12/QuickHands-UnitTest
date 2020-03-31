package com.quickhandslogistics.view.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.ContainerDetailActivity
import com.quickhandslogistics.view.adapter.lumperJobDetailAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_lumper_job_history.*
import java.util.*
import kotlin.collections.ArrayList


class OldLumperJobHistoryActivity :BaseActivity(), lumperJobDetailAdapter.OnAdapterItemClickListener {

    val lumperJobList: ArrayList<String> = ArrayList()
    var faker = Faker()
    private lateinit var lumpersAdapter: lumperJobDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_job_history)

        setupToolbar( faker.name.firstName()+" "+faker.name.lastName())

        lumperData()
        val locale = resources.configuration.locale
        Locale.setDefault(locale)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        recycler_lumper_job_history.apply {
            val linearLayoutManager = LinearLayoutManager(activity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter = lumperJobDetailAdapter(lumperJobList, this@OldLumperJobHistoryActivity)
            adapter = lumpersAdapter
        }


       /* recycler_lumper_job_history.layoutManager = LinearLayoutManager(this)
        recycler_lumper_job_history.adapter = this?.let { lumperJobDetailAdapter(lumperJobList, this@OldLumperJobHistoryActivity) }*/

        linear_root.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@OldLumperJobHistoryActivity,
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

    override fun onItemClick() {
        startIntent(ContainerDetailActivity::class.java)
    }
}
