package com.quickhandslogistics.view.adapter

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
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.*

class CustomerAdapter (var items: ArrayList<CustomerModel>, val context: Context) : Adapter<CustomerViewHolder>() {

    val faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view: View =  LayoutInflater.from(context).inflate(R.layout.item_customer_layout, parent, false)
        return CustomerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun filterList(filteredName: ArrayList<CustomerModel>) {
        this.items = filteredName
        notifyDataSetChanged()
    }

    fun callPhone() {
        val faker = Faker()
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", faker?.phoneNumber?.phoneNumber(), null)
            )
        )
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {

        holder?.lumperText?.text = items.get(position)?.name
        holder?.lumperHours?.text = faker.address.streetAddress()
    }
}

class CustomerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.text_lumper
    var lumperLastName = view.text_last_name
    var profilePic = view.image_lumper_logo
    var constraintRoot = view.constraint_root
    var lumperHours = view.text_shift_hours
}