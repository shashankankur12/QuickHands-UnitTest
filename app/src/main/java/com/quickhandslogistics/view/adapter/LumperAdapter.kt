package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.model.lumper.LumperData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.view.LumperModel
import com.quickhandslogistics.view.activities.LumperDetailsActivity
import com.quickhandslogistics.view.activities.LumperJobHistoryActivity
import com.quickhandslogistics.view.activities.LumperSheetDetailActivity
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.*


class LumperAdapter(var items: List<LumperData>, val context: Context, var lumperJobDetails : String) : Adapter<ViewHolder>() {
    var intent = Intent()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =  LayoutInflater.from(context).inflate(R.layout.item_lumper_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.lumperText?.text = items.get(position).firstName + " " + items.get(position).lastName
        holder.lumperCustId?.text = items.get(position).email
        holder.lumperBuilding?.text = items.get(position).role
        var phone =  items.get(position).phone

        holder.constraintRoot.setOnClickListener {
            if(lumperJobDetails == context.getString(R.string.string_lumper))
                context.startActivity(Intent(context, LumperJobHistoryActivity::class.java))
            else if((lumperJobDetails == context.getString(R.string.string_lumper_sheet)))
                context.startActivity(Intent(context, LumperSheetDetailActivity::class.java))
                else {
                val intent = Intent(context, LumperDetailsActivity::class.java)
                intent.putExtra("lumperData", items.get(position))
                context.startActivity(intent)


            }
        }

        holder.imagePhone.setOnClickListener {
            callPhone(phone)
        }
    }

   /*  fun filterList(filteredName: ArrayList<LumperModel>) {
        this.items = filteredName
        notifyDataSetChanged()
    }*/

    fun callPhone(phone :String) {
        val faker = Faker()
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.text_lumper
    var lumperCustId = view.text_cus_id
    var profilePic = view.image_lumper_logo
    var constraintRoot = view.constraint_root
    var lumperBuilding = view.text_building_name
    var imagePhone = view.image_phone
}