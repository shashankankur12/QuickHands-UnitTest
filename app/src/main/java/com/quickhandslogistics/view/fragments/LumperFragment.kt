package com.quickhandslogistics.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.LumperAdapter
import kotlinx.android.synthetic.main.fragment_lumper.*

class LumperFragment : Fragment() {

    val lumperList: ArrayList<String> = ArrayList();

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
        lumperAttendance()

        recycler_lumper.layoutManager = LinearLayoutManager(context)
        recycler_lumper.adapter = context?.let { LumperAdapter(lumperList, it) }
    }

    fun lumperAttendance() {
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
    }
}