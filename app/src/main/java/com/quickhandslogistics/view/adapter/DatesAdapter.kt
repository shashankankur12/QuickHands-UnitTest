package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.model.DatesModel
import com.quickhandslogistics.model.ScheduledEvents
import com.quickhandslogistics.view.legacy.ScheduleFragment
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DatesAdapter(private val scheduleFragment: ScheduleFragment,
                   private val mDatesList: ArrayList<DatesModel>, private val mActivity: Activity,
                   private val month: Int,
                   private val year: Int,
                   internal var filteredEvents: ArrayList<ScheduledEvents>) : RecyclerView.Adapter<DatesAdapter.DatesHolder>() {

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mActivity)

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): DatesHolder {
        val view = mLayoutInflater.inflate(R.layout.layout_dates, viewGroup, false)
        return DatesAdapter.DatesHolder(view)
    }

    override fun getItemCount(): Int {
        return mDatesList.size
    }

    override fun onBindViewHolder(@NonNull datesHolder: DatesHolder, i: Int) {
        datesHolder.textDate.text = (i + 1).toString()

        if (i == 2 || i == 8) datesHolder.isEventAvailable.visibility = View.VISIBLE

        datesHolder.textDate.setOnClickListener {
            scheduleFragment.setUpEventsRecyclerView(scheduleFragment.recycler_events, scheduleFragment.eventsList)
        }
    }

    class DatesHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textDate = itemView.findViewById<TextView>(R.id.date)
        var isEventAvailable = itemView.findViewById<ImageView>(R.id.event)
    }

    @Throws(ParseException::class)
    private fun getDayName(input: String): String {
        val inFormat = SimpleDateFormat("dd-mm-yyyy")
        val date = inFormat.parse(input)
        val outFormat = SimpleDateFormat("EEE")
        return outFormat.format(date!!)
    }
}
