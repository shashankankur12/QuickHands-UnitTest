package com.quickhandslogistics.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_complete_lumper_job_history_details.*
import kotlinx.android.synthetic.main.layout_header.*

class CompleteCustomerJobHistory : AppCompatActivity() {
    val faker = Faker()
    var lumperSheetDetail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_lumper_job_history_details)

        text_title.text = getString(R.string.string_customer_sheet)

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        if (intent.hasExtra(getString(R.string.string_lumper_sheet))) {
            lumperSheetDetail = intent.getStringExtra(getString(R.string.string_lumper_sheet))

            addLumperSheetDetails()
        }else UpdateLumperSheetDetail()
    }

    fun addLumperSheetDetails(){

        text_name.isEnabled = true
        edit_Date.isEnabled = true
        edit_door.isEnabled = true
        edit_container.isEnabled = true
        edit_cs.isEnabled = true
        edit_po_lots.isEnabled = true
        edit_weight.isEnabled = true
        edit_mix.isEnabled = true
        edit_rush.isEnabled = true
        edit_over_70.isEnabled = true
        edit_start_time.isEnabled = true
        edit_end_time.isEnabled = true
        edit_total_time.isEnabled = true
        edit_lunch_out.isEnabled = true
        edit_lunch_in.isEnabled = true
        edit_notes.isEnabled = true
        edit_sign.isEnabled = true
    }

    fun UpdateLumperSheetDetail(){
        text_name?.setText(faker.name.firstName()+" "+faker.name.lastName() )
        edit_Date?.setText(faker.date.backward(9).toString())
        edit_door.setText(faker.number.digit().toString())
        edit_container.setText(faker.number.digit().toString())
        edit_cs.setText(faker.number.digit().toString())
        edit_po_lots.setText(faker.number.digit().toString())
        edit_weight.setText(faker.number.digit().toString())
        edit_mix.setText(faker.name.firstName().toString())
        edit_rush.setText(faker.name.firstName().toString())
        edit_over_70.setText(faker.number.digit().toString())
        edit_start_time.setText(faker.date.backward(5).toString())
        edit_end_time.setText(faker.date.backward(5).toString())
        edit_total_time.setText(faker.date.backward(5).toString())
        edit_lunch_out.setText(faker.date.backward(5).toString())
        edit_lunch_in.setText(faker.date.backward(5).toString())
        edit_notes.setText(faker.company.name().toString())
        edit_sign.setText(faker.company.name().toString())


        text_name.isEnabled = false
        edit_Date.isEnabled = false
        edit_door.isEnabled = false
        edit_container.isEnabled = false
        edit_cs.isEnabled = false
        edit_po_lots.isEnabled = false
        edit_weight.isEnabled = false
        edit_mix.isEnabled = false
        edit_rush.isEnabled = false
        edit_over_70.isEnabled = false
        edit_start_time.isEnabled = false
        edit_end_time.isEnabled = false
        edit_total_time.isEnabled = false
        edit_lunch_out.isEnabled = false
        edit_lunch_in.isEnabled = false
        edit_notes.isEnabled = false
        edit_sign.isEnabled = false

        text_name.isCursorVisible = false
        edit_Date.isCursorVisible = false
        edit_door.isCursorVisible = false
        edit_container.isCursorVisible = false
        edit_cs.isCursorVisible = false
        edit_po_lots.isCursorVisible = false
        edit_weight.isCursorVisible = false
        edit_mix.isCursorVisible = false
        edit_rush.isCursorVisible = false
        edit_over_70.isCursorVisible = false
        edit_start_time.isCursorVisible = false
        edit_end_time.isCursorVisible = false
        edit_total_time.isCursorVisible = false
        edit_lunch_out.isCursorVisible = false
        edit_lunch_in.isCursorVisible = false
        edit_notes.isCursorVisible = false
        edit_sign.isCursorVisible = false
    }
}
