package com.adsuclearance

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.gson.Gson
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_my_profile.*

class ActivityMyProfile : AppCompatActivity() {
    lateinit var thisContext: Activity
    lateinit var curStudentDetail:StudentRegisterClassBinder
    lateinit var prefs: ClassSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        Slidr.attach(this)//for slidr swipe lib

        thisContext = this
        prefs = ClassSharedPreferences(thisContext)


        arrow_back.setOnClickListener {
            super.onBackPressed()
        }

        val stDetails = ClassSharedPreferences(thisContext).getUserJSONDetails()
        try {
            curStudentDetail = Gson().fromJson(stDetails, Array<StudentRegisterClassBinder>::class.java).asList()[0]

            loadStData()
        } catch (e: Exception) {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadStData() {
        fullname.text = "${curStudentDetail.surname?.toUpperCase()}  ${curStudentDetail.firstname}"
        reg_no1.text = curStudentDetail.reg_no?.toUpperCase()
        reg_no2.text = curStudentDetail.reg_no?.toUpperCase()


        first_name.text = curStudentDetail.firstname
        sur_name.text = curStudentDetail.surname
        other_name.text = if(curStudentDetail.othername!!.isNotEmpty()){curStudentDetail.othername}else{"Not set"}
        phone_number.text = curStudentDetail.phone_number
        gender.text = curStudentDetail.gender
        department.text = curStudentDetail.department
        faculty.text = curStudentDetail.faculty
    }


}
