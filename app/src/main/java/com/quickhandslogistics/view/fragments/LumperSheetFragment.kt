package com.quickhandslogistics.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.LumperModel
import com.quickhandslogistics.view.adapter.LumperSheetAdapter
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.fragment_lumper_sheet2.*
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.quickhandslogistics.view.activities.LumperDetailsActivity
import com.quickhandslogistics.view.activities.LumperListActivity
import com.quickhandslogistics.view.activities.LumperSheetDetailActivity


class LumperSheetFragment : Fragment() {
    val lumperList: ArrayList<LumperModel> = ArrayList()
    val lumperStatusList: ArrayList<String> = ArrayList()
    lateinit var lumperSheetAdapter: LumperSheetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_lumper_sheet2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchLumper()
        lumperStatusSheet()

        recycler_lumper_sheet.layoutManager = LinearLayoutManager(context)

        val faker = Faker()
        for (i in 1..20) {
            lumperList.add(LumperModel(faker.name.firstName(), faker.name.lastName()))
        }

        lumperSheetAdapter =  LumperSheetAdapter(lumperList, context!!, lumperStatusList)
        recycler_lumper_sheet.adapter = context?.let { lumperSheetAdapter}

        image_cancel.setOnClickListener {
            edit_search_lumper.text.clear()
            val imm =
                context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edit_search_lumper!!.windowToken, 0)
        }

        fab_show_lumper.setOnClickListener {
            context!!.startActivity(Intent(context, LumperListActivity::class.java))
        }

        image_filter.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(context!!,image_filter)
            popupMenu.menuInflater.inflate(R.menu.filtermenu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.menu_complete ->
                        Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    R.id.mnu_prgrs ->
                        Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                }
                true
            })
            popupMenu.show()
        }
    }


    fun searchLumper() {
        edit_search_lumper.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
                if(edit_search_lumper.text.isNullOrEmpty()) {
                    val imm =
                        context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

        lumperSheetAdapter.filterLumperList(filterName)
        if(filterName.isEmpty()) {
            text_no_record_found?.visibility = View.VISIBLE
        } else {
            text_no_record_found?.visibility = View.GONE
        }
    }


    fun filterStatus(text:String) {

        var filterStatus = ArrayList<LumperModel>()

        for (s in lumperList) {
            var fullName = s.name.toLowerCase()+ " "+ s.lastName.toLowerCase()
            if (s.name.toLowerCase().contains(text.toLowerCase()) || s.lastName.toLowerCase().contains(text.toLowerCase())) {
                filterStatus.add(s)
            } else if (fullName.contains(text.toLowerCase())) {
                filterStatus.add(s)
            }
        }

        lumperSheetAdapter.filterLumperList(filterStatus)
        if(filterStatus.isEmpty()) {
            text_no_record_found?.visibility = View.VISIBLE
        } else {
            text_no_record_found?.visibility = View.GONE
        }
    }

    fun lumperStatusSheet() {
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
