package com.quickhandslogistics.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.model.CustomerModel
import com.quickhandslogistics.view.LumperModel
import com.quickhandslogistics.view.activities.CustomerActivity
import com.quickhandslogistics.view.adapter.CustomerSheetAdapter
import com.quickhandslogistics.view.adapter.LumperSheetAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.fragment_customer_sheet.*
import kotlinx.android.synthetic.main.fragment_lumper_sheet2.*
import java.util.Locale.filter

class CustomerSheetFragment : Fragment() {
    val lumperList: ArrayList<CustomerModel> = ArrayList()
    val lumperStatusList: ArrayList<String> = ArrayList()
    lateinit var customerSheetAdapter: CustomerSheetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerStatusSheet()
        recycler_customer_sheet.layoutManager = LinearLayoutManager(context)

        val faker = Faker()
        for (i in 1..20) {
            lumperList.add(CustomerModel(faker.company.name()))
        }

        customerSheetAdapter =  CustomerSheetAdapter(lumperList, context!!, lumperStatusList)
        recycler_customer_sheet.adapter = context?.let { customerSheetAdapter}

        fab_add_customer.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, CustomerActivity::class.java)
            activity?.startActivity(intent)
        })
    }

    fun customerStatusSheet() {
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
        lumperStatusList.add(getString(R.string.in_progress))
        lumperStatusList.add(getString(R.string.complete))
    }
}
