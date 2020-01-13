package com.quickhandslogistics.utils.ui.lumper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.ui.adapter.LumperAdapter
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
        lumperList.add("1")
        lumperList.add("2")
        lumperList.add("3")
        lumperList.add("4")
        lumperList.add("5")
        lumperList.add("6")
        lumperList.add("7")
        lumperList.add("8")
        lumperList.add("9")
        lumperList.add("10")
        lumperList.add("11")
        lumperList.add("12")
    }
}