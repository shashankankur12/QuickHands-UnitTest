package com.quickhandslogistics.adapters.customerContact

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.customerContact.CustomerContactContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.item_customer_contact_layout.view.*

import kotlin.collections.ArrayList

class CustomerContactAdapter(val resources: Resources, var adapterItemClickListener: CustomerContactContract.View.OnAdapterItemClickListener) : Adapter<CustomerContactAdapter.ViewHolder>() {
    private var items: ArrayList<EmployeeData> = ArrayList()
    private var filteredItems: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_contact_layout, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return  /*items.size*/ 3
    }

    private fun getItem(position: Int): EmployeeData {
        return  items[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(/*getItem(position)*/)
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewCustomerName: TextView = view.textViewCustomerName
        private val textViewMessageTime: TextView = view.textViewMessageTime
        private val textViewEmployeeRole: CustomTextView = view.textViewEmployeeRole
        private val textViewEmployeeTitle: CustomTextView = view.textViewEmployeeTitle
        private val textViewEmployeeShift: CustomTextView = view.textViewEmployeeShift
        private val textViewEmail: CustomTextView = view.textViewEmail
        private val textVieWContact: CustomTextView = view.textVieWContact
        private val imageViewCall: ImageView = view.imageViewCall

        fun bind(/*employeeData: EmployeeData*/) {
            val mobilenumber ="0908897096"

            textViewCustomerName.text= "Namit"
            textViewEmployeeRole.text= "Manager"
            textViewEmployeeTitle.text= "Shipping"
            textViewEmployeeShift.text= String.format(resources.getString(R.string.shift_normal),"Day")
            textViewEmail.text= "namit@yopmail.com"
//            textVieWContact.text= PhoneNumberUtils.formatNumber(mobilenumber, "US")
            textVieWContact.text= UIUtils.formetMobileNumber(mobilenumber)
            textViewMessageTime.text= "12:22 PM"

            imageViewCall.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
//                        val lumperData = getItem(adapterPosition)
//                        adapterItemClickListener.onItemClick(lumperData)
                    }
                    imageViewCall.id -> {
//                        val lumperData = getItem(adapterPosition)
//                        lumperData.phone?.let { phone ->
//                            adapterItemClickListener.onPhoneViewClick(textViewLumperName.text.toString(), phone)
//                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun updateLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.items.clear()
        this.items.addAll(employeeDataList)
        notifyDataSetChanged()
    }
}