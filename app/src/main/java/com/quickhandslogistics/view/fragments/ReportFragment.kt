package com.quickhandslogistics.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.ReportAdapter
import kotlinx.android.synthetic.main.fragment_send.*

import java.util.*
import kotlin.collections.ArrayList


class ReportFragment : Fragment() {

    val reportList: ArrayList<String> = ArrayList()
    val reportImages = ArrayList(
        Arrays.asList(
            R.drawable.job_history,
            R.drawable.lumper_history))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_send, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportSheet()

        recycler_report.layoutManager = LinearLayoutManager(context)
        recycler_report.adapter = context?.let { ReportAdapter(reportList,reportImages, it) }
    }

    fun reportSheet() {
        reportList.add(getString(R.string.job_history))
        reportList.add(getString(R.string.lumper_job_history))
    }
}