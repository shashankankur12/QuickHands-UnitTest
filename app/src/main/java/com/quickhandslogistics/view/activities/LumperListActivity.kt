package com.quickhandslogistics.view.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.view.adapter.LumperDummyAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_lumper_list.*
import kotlinx.android.synthetic.main.layout_header.*

class LumperListActivity : AppCompatActivity() {
    val lumperList: ArrayList<LumperModel> = ArrayList()
    val arrayList: ArrayList<String> = ArrayList()
    var lumperJobDetail: String = ""
    lateinit var lumperAdapter: LumperDummyAdapter

    companion object {
        const val ARG_STRING_LUMPER = "ARG_STRING_LUMPER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_list)

        if (intent.hasExtra(getString(R.string.string_lumpers)))
            lumperJobDetail = intent.getStringExtra(getString(R.string.string_lumpers))

        if (intent.hasExtra(getString(R.string.string_lumper_sheet)))
            lumperJobDetail = intent.getStringExtra(getString(R.string.string_lumper_sheet))

        text_title?.setText(getString(R.string.choose_lumpers))

        searchLumper()

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        image_cancel.setOnClickListener {
            edit_search_lumper.text.clear()
            val imm =
                this@LumperListActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edit_search_lumper!!.windowToken, 0)
        }

        recycler_lumper.layoutManager = LinearLayoutManager(this@LumperListActivity)

        val faker = Faker()
        for (i in 1..20) {
            lumperList.add(
                LumperModel(
                    faker.name.firstName(),
                    faker.name.lastName(),""
                )
            )
        }

        lumperAdapter = LumperDummyAdapter(lumperList, this@LumperListActivity, lumperJobDetail)
        recycler_lumper.adapter = this@LumperListActivity.let { lumperAdapter }
    }

    fun searchLumper() {
        edit_search_lumper.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
                if (edit_search_lumper.text.isNullOrEmpty()) {
                    val imm =
                        this@LumperListActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edit_search_lumper!!.windowToken, 0)
                    image_cancel.visibility = View.GONE
                } else {
                    image_cancel.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    fun filter(text:String) {

        var filterName = ArrayList<LumperModel>()

        for (s in lumperList) {
            var fullName = s.name.toLowerCase()+ " "+ s.lastName.toLowerCase()
            if (s.name.toLowerCase().contains(text.toLowerCase()) || s.lastName.toLowerCase().contains(text.toLowerCase())) {
                filterName.add(s)
            } else if (fullName.contains(text.toLowerCase())) {
                filterName.add(s)
            }
        }

        lumperAdapter.filterList(filterName)
        if(filterName.isEmpty()) {
            text_no_record_found?.visibility = View.VISIBLE
        } else {
            text_no_record_found?.visibility = View.GONE
        }
    }
}
