package com.quickhandslogistics.adapters.lumperSheet

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_notes.*
import kotlinx.android.synthetic.main.item_building_operation.view.*
import java.util.*

class CorrectionBuildingOpsAdapter(private var parameters: ArrayList<String>,val context: Context) : Adapter<CorrectionBuildingOpsAdapter.ViewHolder>() {

    private var data = HashMap<String, String>()
    private var orignalData = HashMap<String, String>()
    var isCompleted :Boolean?= false

    init {
//        parameters.sortWith(Comparator { value1: String, value2: String ->
//            value1.toLowerCase().compareTo(value2.toLowerCase())
//        })

//        parameters= ScheduleUtils.sortAccordingly(parameters)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_building_operation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parameters.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), TextWatcher {
        private val textViewHeader: TextView = view.textViewHeader
        private val editTextValue: EditText = view.editTextValue
        private val mainRootLayout: LinearLayout = view.mainRootLayout

        fun bind() {
            val header = parameters[adapterPosition]
            textViewHeader.text = header.capitalize()
            if (header.equals("Cases")){
                editTextValue.inputType = InputType.TYPE_CLASS_NUMBER;
            }else{
                editTextValue.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME;
            }

            if (data.containsKey(header)) {
                editTextValue.setText(data[header])
            }else{
                editTextValue.setText("")
            }
            editTextValue.addTextChangedListener(this)

            if(isCompleted == true){
                editTextValue.isEnabled =false
                textViewHeader.isEnabled =false
                mainRootLayout.background = ContextCompat.getDrawable(context, R.drawable.schedule_item_background_grey)
            }else {
                editTextValue.isEnabled =true
                textViewHeader.isEnabled =true
                mainRootLayout.background = ContextCompat.getDrawable(context, R.drawable.schedule_item_background)
            }
        }

        override fun afterTextChanged(text: Editable?) {
            val header = parameters[adapterPosition]
            if (!data.containsKey(header)) {
                data[header] = ""
            }
            data[header] = text.toString()

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun updateData(data: HashMap<String, String> ,isCompleted:Boolean?) {
        this.data.clear()
        this.data.putAll(data)
        this.orignalData.putAll(data)
        this.isCompleted=isCompleted
        notifyDataSetChanged()
    }

    fun getUpdatedData(): HashMap<String, String> {
        return data
    }

    fun compareChanges(): Boolean {
        val valueSet1: HashSet<String> = HashSet<String>(orignalData.values)
        val valueSet2: HashSet<String> = HashSet<String>(data.values)
        valueSet1.remove("")
        valueSet2.remove("")
        return valueSet1.equals(valueSet2)
    }
}