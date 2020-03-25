package com.quickhandslogistics.modified.views.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.models.lumpers.LumperListModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumperListAdapter (var adapterItemClickListener: OnAdapterItemClickListener) :
    RecyclerView.Adapter<LumperListAdapter.WorkItemHolder>() {

    var lumpersList: ArrayList<LumperListModel> = ArrayList()
    var lumpersListFilter: ArrayList<LumperListModel> = ArrayList()
    private var searchEnabled = false
    private var searchTerm = ""

    init {
        lumpersList.add(LumperListModel("Gene ","Hand", "99896945685"))
        lumpersList.add(LumperListModel("Frida", "Moore","3845798347593"))
        lumpersList.add(LumperListModel("Virgil", "Ernser","3745638476584"))
        lumpersList.add(LumperListModel("Philip", "Von","56348563485684"))
    }

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_layout, parent, false)
        return WorkItemHolder(view)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) lumpersListFilter.size else lumpersList.size
    }

    private fun getItem(position: Int): LumperListModel {
        return if (searchEnabled) lumpersListFilter[position] else lumpersList[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {

        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            adapterItemClickListener.onItemClick()
        }

        holder.imageViewCall.setOnClickListener {
            adapterItemClickListener.onPhoneViewClick(
                lumpersList[position].name+ " "+lumpersList[position].lastName,
                lumpersList[position].phone
            )
        }
    }

    class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view){
        var textViewLumperName: TextView = view.textViewLumperName
        var textViewEmployeeId: TextView = view.textViewEmployeeId
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        var textViewShiftHours: TextView = view.textViewShiftHours
        var imageViewCall: ImageView = view.imageViewCall

        fun bind(lumperModelData: LumperListModel) {
            textViewLumperName.text  = String.format(
                "%s %s",
                lumperModelData.name.toUpperCase(Locale.getDefault()),
                lumperModelData.lastName.toUpperCase(Locale.getDefault())
            )

            Picasso.get().load(R.drawable.ic_basic_info_placeholder)
                .error(R.drawable.ic_basic_info_placeholder)
                .into(circleImageViewProfile)
        }
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            lumpersListFilter.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        lumpersListFilter.clear()
        if (searchTerm.isEmpty()) {
            lumpersListFilter.addAll(lumpersList)
        } else {
            for (data in lumpersList) {
                val term = "${data.name} ${data.lastName}"

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    lumpersListFilter.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    interface OnAdapterItemClickListener {
        fun onItemClick()
        fun onPhoneViewClick(lumperName: String, phone: String)
    }
}