package com.quickhandslogistics.view.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.model.CustomerModel
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.adapter.CustomerAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_customer.*
import kotlinx.android.synthetic.main.layout_header.*

class CustomerActivity : AppCompatActivity() {

    val customerList: ArrayList<CustomerModel> = ArrayList()
    var lumperJobDetail: String = ""
    lateinit var customerAdapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        text_title.setText("Choose Customer")

        searchLumper()

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        recycler_customer.layoutManager = LinearLayoutManager(this)

        val faker = Faker()
        for (i in 1..20) {
            customerList.add(CustomerModel(faker.company.name()))
        }

        customerAdapter = CustomerAdapter(customerList, this)
        recycler_customer.adapter = this?.let {
            customerAdapter
        }

        image_cancel_customer.setOnClickListener {
            edit_search_customer.text.clear()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edit_search_customer!!.windowToken, 0)
        }
    }

    fun searchLumper() {
        edit_search_customer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
                if (edit_search_customer.text.isNullOrEmpty()) {
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edit_search_customer!!.windowToken, 0)
                    image_cancel_customer.visibility = View.GONE
                } else {
                    image_cancel_customer.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun filter(text: String) {

        var filterName = ArrayList<CustomerModel>()

        for (s in customerList) {
            var fullName = s.name.toLowerCase()
            if (s.name.toLowerCase().contains(text.toLowerCase())) {
                filterName.add(s)
            } else if (fullName.contains(text.toLowerCase())) {
                filterName.add(s)
            }
        }

        customerAdapter.filterList(filterName)
    }
}
