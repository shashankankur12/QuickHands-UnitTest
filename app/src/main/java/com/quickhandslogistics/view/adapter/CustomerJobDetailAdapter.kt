package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.CompleteCustomerJobHistory
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_job_history.view.*

class CustomerJobDetailAdapter (val items: ArrayList<String>, val context: Context) : RecyclerView.Adapter<CustomerJobDetailAdapter.CustomerjobViewHolder>() {

    var faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerjobViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_job_history, parent, false)
        return CustomerjobViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomerjobViewHolder, position: Int) {
        holder?.lumperContainerNumber?.text = faker.number.hexadecimal(8).toUpperCase()
        //holder?.lumperDoor.text = faker.number.digit()
        //holder?.lumperWeight.text = faker.number.digit()

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, CompleteCustomerJobHistory::class.java))
        }
    }

    class CustomerjobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperContainerNumber = view.input_container_number
        var lumperDoor = view.textViewDoor
        var lumperWeight = view.textViewWeight
        var lumperTime =  view.textViewTime
    }
}