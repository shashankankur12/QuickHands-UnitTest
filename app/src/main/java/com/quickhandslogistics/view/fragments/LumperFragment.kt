package com.quickhandslogistics.view.fragments

import android.content.Context
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
import com.quickhandslogistics.view.LumperModel
import com.quickhandslogistics.view.adapter.LumperAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.fragment_lumper.*


class LumperFragment : Fragment() {

    val lumperList: ArrayList<LumperModel> = ArrayList()
    var lumperJobDetail: String = ""
    lateinit var lumperAdapter:LumperAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_lumper, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments

        if(bundle != null)
        lumperJobDetail = bundle!!.getString("lumper_detail").toString()

       // lumperAttendance()
        searchLumper()

        recycler_lumper.layoutManager = LinearLayoutManager(context)

        val faker = Faker()
        for (i in 1..20) {
            lumperList.add(LumperModel(faker.name.firstName(), faker.name.lastName()))
        }

        lumperAdapter =  LumperAdapter(lumperList, context!!,lumperJobDetail)
        recycler_lumper.adapter = context?.let { lumperAdapter}

        image_cancel.setOnClickListener {
           edit_search_lumper.text.clear()
            val imm =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edit_search_lumper!!.windowToken, 0)
            }
        }

    fun searchLumper() {
        edit_search_lumper.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
                if(edit_search_lumper.text.isNullOrEmpty()) {
                    val imm =
                        activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edit_search_lumper!!.windowToken, 0)
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