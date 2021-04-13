package com.quickhandslogistics.adapters.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.common.LumperImagesContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.content_lumper_work_detail.*
import kotlinx.android.synthetic.main.item_schdule_lumper_image_list.view.*

class LumperImagesAdapter(var lumpersList: ArrayList<EmployeeData>, private val sharedPref: SharedPref, var onItemClickListener: LumperImagesContract.OnItemClickListener) :
    RecyclerView.Adapter<LumperImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_schdule_lumper_image_list, viewGroup, false)
        return ViewHolder(view, viewGroup.context)
    }

    override fun getItemCount(): Int {
        return if (lumpersList.size > 11) 11 else lumpersList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return lumpersList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val circleImageViewProfile: CircleImageView = itemView.circleImageViewProfile
        private val textViewNumber: TextView = itemView.textViewNumber

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(employeeData: EmployeeData) {
            if (adapterPosition > 9) {
                Glide.with(context).clear(circleImageViewProfile);
                circleImageViewProfile.visibility = View.GONE
                textViewNumber.visibility = View.VISIBLE
                textViewNumber.text = "+${lumpersList.size - 10}"
            } else {
                UIUtils.showEmployeeProfileImage(context, employeeData.profileImageUrl, circleImageViewProfile)
                 var buildingId =if (!sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID).isNullOrEmpty()) sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID) else ""
                UIUtils.updateProfileBorder(context, !buildingId.equals(employeeData.buildingIdAsLumper), circleImageViewProfile)
                circleImageViewProfile.visibility = View.VISIBLE
                textViewNumber.visibility = View.GONE
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> onItemClickListener.onLumperImageItemClick(lumpersList)
                }
            }
        }
    }

    fun updateData(lumpersList: ArrayList<EmployeeData>) {
        this.lumpersList.clear()
        this.lumpersList.addAll(lumpersList)
        notifyDataSetChanged()
    }
}