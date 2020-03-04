package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.model.CustomerModel
import com.quickhandslogistics.view.activities.CustomerBuildingsActivity
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.*

class CustomerAdapter (var items: ArrayList<CustomerModel>,val mActivity: Activity) : Adapter<CustomerViewHolder>() {

    val faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view: View =  LayoutInflater.from(mActivity).inflate(R.layout.item_customer_layout, parent, false)
        return CustomerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun filterList(filteredName: ArrayList<CustomerModel>) {
        this.items = filteredName
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.lumperText.text = items.get(position).name
//        holder.lumperHours.text = faker?.address.streetAddress()
       /* holder.constraintRoot.setOnClickListener(View.OnClickListener { view ->
            mActivity.finish()
        })*/

        holder.constraintRoot.setOnClickListener {
            mActivity.startActivity(Intent(mActivity, CustomerBuildingsActivity::class.java).putExtra("name", holder.lumperText?.text))
        }
    }
}

class CustomerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.text_lumper
    var lumperLastName = view.text_cus_id
    var profilePic = view.image_lumper_logo
    var constraintRoot = view.constraint_root
    var lumperHours = view.text_building_name
}