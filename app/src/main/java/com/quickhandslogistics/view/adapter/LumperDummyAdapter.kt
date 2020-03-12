package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.*


class LumperDummyAdapter(
    var items: List<LumperModel>,
    val context: Activity,
    var lumperJobDetails: String
) : Adapter<LumperDummyAdapter.ViewHolder>() {
    var intent = Intent()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_lumper_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.lumperText?.text =
            items.get(position).name.capitalize() + " " + items.get(position).lastName.capitalize()
        holder.lumperCustId?.text = "lumer@lumer.com"
        holder.lumperBuilding?.text = "Lumper"

        holder.constraintRoot.setOnClickListener {
            Utils.finishActivity(context)

        }
        holder.imagePhone.visibility = View.GONE
    }

    fun filterList(filteredName: ArrayList<LumperModel>) {
        this.items = filteredName
        notifyDataSetChanged()
    }

    fun callPhone(phone: String) {
        val faker = Faker()
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        )
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperText = view.text_lumper
        var lumperCustId = view.text_cus_id
        var profilePic = view.image_lumper_logo
        var constraintRoot = view.constraint_root
        var lumperBuilding = view.text_building_name
        var imagePhone = view.image_phone
    }
}