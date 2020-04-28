package com.quickhandslogistics.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_job_history.view.*

class lumperJobDetailAdapter(val items: ArrayList<String>, private var adapterItemClickListener: OnAdapterItemClickListener  )
    : RecyclerView.Adapter<lumperJobDetailAdapter.JobViewHolder>() {

    var faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_job_history, parent, false)
        return JobViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
       // holder?.lumperContainerNumber?.text = faker.number.hexadecimal(8).toUpperCase()
       // holder?.lumperDoor.text = faker.number.digit()
      //  holder?.lumperWeight.text = faker.number.digit()

        holder.itemView.setOnClickListener {
            adapterItemClickListener.onItemClick()
            //context.startActivity(Intent(context, ContainerDetailActivity::class.java))
        }
        }

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperContainerNumber = view.input_container_number
        var lumperDoor = view.textViewDoor
        var lumperWeight = view.textViewWeight
        var lumperTime =  view.textViewTime
    }

    interface OnAdapterItemClickListener {
        fun onItemClick()
    }
}