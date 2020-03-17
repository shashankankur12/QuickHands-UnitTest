package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.model.CustomerModel
import com.quickhandslogistics.view.activities.CustomerBuildingsActivity
import de.hdodenhof.circleimageview.CircleImageView
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_customer_layout.view.*
import kotlinx.android.synthetic.main.item_lumper_layout.view.*
import kotlinx.android.synthetic.main.item_lumper_layout.view.constraint_root

class CustomerAdapter(var items: ArrayList<CustomerModel>, val mActivity: Activity) :
    Adapter<CustomerViewHolder>() {

    val faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view: View =
            LayoutInflater.from(mActivity).inflate(R.layout.item_customer_layout, parent, false)
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
           if(!TextUtils.isEmpty(holder.lumperText.text)) {
               mActivity.startActivity(
                   Intent(
                       mActivity,
                       CustomerBuildingsActivity::class.java
                   ).putExtra("name", holder.lumperText.text.toString())
               )
           }
        }
    }
}

class CustomerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var constraintRoot = view.constraint_root
    var lumperText: TextView = view.text_lumper
    var lumperLastName: TextView = view.text_last_name
    var profilePic: ImageView = view.image_lumper_logo
    var lumperHours: TextView = view.text_shift_hours
}