package com.quickhandslogistics.view.adapter

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
import com.quickhandslogistics.view.activities.LumperDetailsActivity
import com.quickhandslogistics.view.activities.LumperJobHistoryActivity
import com.squareup.picasso.Picasso
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.*


class LumperAdapter(val items: ArrayList<String>, val context: Context, var lumperJobDetails : String) : Adapter<ViewHolder>() {

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_lumper_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.lumperText?.text = faker.name.firstName() + " " + faker.name.lastName()
        //holder?.lumperHours?.text = faker.time.between()

        Picasso.get().load(faker.avatar.image()).error(R.drawable.ic_basic_info_placeholder).into(holder?.profilePic)

        holder.constraintRoot.setOnClickListener {
            if(lumperJobDetails == "lumper")
            context.startActivity(Intent(context, LumperJobHistoryActivity::class.java))
            else   context.startActivity(Intent(context, LumperDetailsActivity::class.java))
        }

        holder.imagePhone.setOnClickListener {
            callPhone()
        }
    }

    fun callPhone() {
        context.startActivity(
            Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", faker.phoneNumber.phoneNumber(), null)
            )
        )
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.text_lumper
    var lumperLastName = view.text_last_name
    var profilePic = view.image_lumper_logo
    var constraintRoot = view.constraint_root
    var lumperHours = view.text_shift_hours
    var imagePhone = view.image_phone
}