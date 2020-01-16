package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.CompleteLumperJobHistoryDetails
import com.quickhandslogistics.view.activities.LumperDetailsActivity
import com.quickhandslogistics.view.activities.LumperJobHistoryActivity
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.lumper_job_history_item.view.*

class lumperJobDetailAdapter(val items: ArrayList<String>, val context: Context) : RecyclerView.Adapter<lumperJobDetailAdapter.JobViewHolder>() {

    var faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.lumper_job_history_item, parent, false)
        return JobViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder?.lumperTextName?.text = faker.name.firstName()
        holder?.lumperDoor.text = faker.number.digit()
        holder?.lumperWeight.text = faker.number.digit()
        holder?.lumperStartTime.text = faker.time.forward(5).toString()
        holder?.lumperEndTime.text = faker.time.backward(2).toString()
        holder.constraintRoot.setOnClickListener {
            context.startActivity(Intent(context, CompleteLumperJobHistoryDetails::class.java))
        }
        }

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperTextName = view.text_name
        var lumperDoor = view.text_door
        var lumperWeight = view.text_job_weight_kg
        var constraintRoot = view.constraint_root_item
        var lumperStartTime = view.text_start
        var lumperEndTime = view.text_end_time
    }

}