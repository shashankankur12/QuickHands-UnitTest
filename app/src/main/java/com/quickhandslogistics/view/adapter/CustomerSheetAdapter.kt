package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.model.CustomerModel
import com.quickhandslogistics.modified.data.lumperSheet.StatusModel
import com.quickhandslogistics.view.activities.CustomerLoadActivity
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_customer_sheet.view.*

class CustomerSheetAdapter(var items: ArrayList<CustomerModel>, val context: Context, val statusItems: ArrayList<StatusModel>) : RecyclerView.Adapter<CustomerSheetAdapter.CustomerSheetViewHolder>() {

    var faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerSheetViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_customer_sheet, parent, false)
        return CustomerSheetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomerSheetAdapter.CustomerSheetViewHolder, position: Int) {

        holder?.lumperName?.text = items.get(position)?.name
        holder?.lumperDate?.text = faker.address.streetAddress()
        holder?.lumperStatus.text = statusItems.get(position).status.toUpperCase()

        if(TextUtils.equals(statusItems.get(position).status, context.resources.getString(R.string.complete))){
            holder?.lumperStatus.setBackgroundResource(R.drawable.chip_complete)
        }else  holder?.lumperStatus.setBackgroundResource(R.drawable.chip_in_progress)

        holder.constraintRoot.setOnClickListener {

            context.startActivity(Intent(context, CustomerLoadActivity::class.java))
        }
    }

    class CustomerSheetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperName = view.text_lumper_name
        var profilePic = view.image_company_logo
        var constraintRoot = view.constraint_root
        var lumperDate = view.text_date
        var lumperStatus = view.text_status
    }
}
