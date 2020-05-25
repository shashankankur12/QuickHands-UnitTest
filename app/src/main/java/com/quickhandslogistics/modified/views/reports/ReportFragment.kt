package com.quickhandslogistics.modified.views.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_reports.*

class ReportFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewJobHistory.setOnClickListener(this)
        textViewLumperJobHistory.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewJobHistory.id -> {
                }
                textViewLumperJobHistory.id -> {
                }
            }
        }
    }
}