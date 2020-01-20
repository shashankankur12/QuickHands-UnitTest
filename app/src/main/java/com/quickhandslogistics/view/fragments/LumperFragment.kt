package com.quickhandslogistics.view.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.LumperModel
import com.quickhandslogistics.view.activities.DashboardActivity
import com.quickhandslogistics.view.adapter.LumperAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.fragment_lumper.*
import kotlinx.android.synthetic.main.layout_header.*


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

       // lumperAttendance()
        searchLumper()

        recycler_lumper.layoutManager = LinearLayoutManager(context)

        val faker = Faker()
        for (i in 1..20) {
            lumperList.add(LumperModel(faker.name.firstName(), faker.name.lastName()))
        }

        lumperAdapter =  LumperAdapter(lumperList, context!!,lumperJobDetail)
        recycler_lumper.adapter = context?.let { lumperAdapter}

    }

    fun searchLumper() {
        edit_search_lumper.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
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



  /*  fun lumperAttendance() {
        lumperList.add("Nigel")
        lumperList.add("Mason")
        lumperList.add("Brent")
        lumperList.add("Denton")
        lumperList.add("Herman")
        lumperList.add("Cody")
        lumperList.add("Griffin")
        lumperList.add("Fletcher")
        lumperList.add("Leroy")
        lumperList.add("Nissim")
        lumperList.add("Brock")
        lumperList.add("Orson")
        lumperList.add("Jasper")
        lumperList.add("Dexter")
        lumperList.add("Fulton")
        lumperList.add("Caleb")
        lumperList.add("Alfonso")
        lumperList.add("Bert")
        lumperList.add("Bert")
        lumperList.add("Berk")
        lumperList.add("Hall")
        lumperList.add("Valentine")
        lumperList.add("Yasir")
        lumperList.add("Michael")
        lumperList.add("Beck")
        lumperList.add("Buckminster")
        lumperList.add("Zeus")
        lumperList.add("Carl")
        lumperList.add("Raphael")
        lumperList.add("Quinn")
    }*/
}