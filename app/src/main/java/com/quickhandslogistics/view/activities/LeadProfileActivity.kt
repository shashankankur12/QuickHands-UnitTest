package com.quickhandslogistics.view.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.GalleryPicker
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_lead_profile.*
import kotlinx.android.synthetic.main.activity_lead_profile.text_title
import kotlinx.android.synthetic.main.layout_header.*

class LeadProfileActivity : AppCompatActivity(), GalleryPicker.GalleryPickerListener {

    var faker = Faker()
    var galleryPicker:GalleryPicker?= null
    private var imagePath: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_profile)

        text_title.text = "Profile"

        edit_first_name.setText("John")
        edit_last_name.setText("William")
        edit_title.setText("Lead Manager")
        edit_email.setText("john.william@quickhands.com")
        edit_lead_emp_id.setText("JW_1001")
        edit_shift_hours.setText("09:00 AM TO 06:00 PM")
        edit_district_manager.setText("Garrett Hamill")
        edit_location.setText("3200 E. Guasti RD Suite 100, Ontario, CA 91761")

        image_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }
    }

    override fun onMediaSelected(imagePath: String?, uri: Uri?, isImage: Boolean) {
        if (isImage) {
            this.imagePath = imagePath
            image_lead_profile.setImageURI(uri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) galleryPicker?.fetch(requestCode, data)
    }
}
