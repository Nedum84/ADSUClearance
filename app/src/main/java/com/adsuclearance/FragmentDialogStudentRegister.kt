package com.adsuclearance


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.*
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Window
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_dialog_student_register.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class FragmentDialogStudentRegister: DialogFragment(){
    lateinit var thisContext: Activity
    private var listener: FragmentStRegisterInteractionListener? = null

    var stGender: String = "-1"
    var stNationality: String = "-1"
    var stMaritalStatus: String = "-1"
    var stReligion = "-1"
    var stModeOfEntry = "-1"
    var stProgramType = "-1"
    var stYearOfEntry = "-1"
    var stCourseDuration = "-1"
    var stAwardInView = "-1"
    var stDepartment = "-1"
    var stFaculty = "-1"






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_student_register, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = activity!!

        closeDialogFrag.setOnClickListener {
            dialog!!.dismiss()
        }
        closeDialogFrag2.setOnClickListener {
            dialog!!.dismiss()
        }

        registerBtn.setOnClickListener {
            processRegistration()
        }
        loginBtn.setOnClickListener {
            processLogin()
        }
        stAlreadyRegisteredBtn.setOnClickListener {
            stRegisterWrapper.visibility = View.GONE
            stLoginWrapper.visibility = View.VISIBLE
        }
        stNotRegisteredBtn.setOnClickListener {
            stRegisterWrapper.visibility = View.VISIBLE
            stLoginWrapper.visibility = View.GONE
        }


        initializeData()
    }
    private fun toString(editText: EditText):String{
        return editText.text.toString().trim()
    }
    private fun checkEmpty(string: String):Boolean{
        return string.isEmpty()
    }


    private fun processRegistration() {
        val stSurname = toString(st_surname)
        val stFirstName = toString(st_firstname)
        val stOtherName = toString(st_othername)
        val stRegNo = toString(st_regno)
        val stPhoneNo = toString(st_phone_number)
        val stDateOfBirth = toString(st_dob)
        val stPlaceOfBirth = toString(st_place_of_birth)
        val stPlaceOfOrigin = toString(st_place_of_origin)
        val stNextOfKinName = toString(st_next_of_kin_name)
        val stNextOfKinAddr = toString(st_next_of_kin_addr)
        val stSponsorName = toString(st_sponsor_name)
        val stSponsorAddr = toString(st_sponsor_addr)

        if(checkEmpty(stSurname)){
            ClassAlertDialog(thisContext).toast("Surname is required")
        }else if(checkEmpty(stFirstName)){
            ClassAlertDialog(thisContext).toast("First name is required")
        }else if(checkEmpty(stRegNo)){
            ClassAlertDialog(thisContext).toast("Reg No. is required")
        }else if(checkEmpty(stPhoneNo)){
            ClassAlertDialog(thisContext).toast("Your phone number is required")
        }else if(stGender == "Gender"){
            ClassAlertDialog(thisContext).toast("Select your gender...")
        }else if(stDepartment == "Department"){
            ClassAlertDialog(thisContext).toast("Select your department...")
        }else if(stFaculty =="Faculty"){
            ClassAlertDialog(thisContext).toast("Select your faculty...")
        }else{

            //creating volley string request
            val dialog= ClassProgressDialog(context)
            dialog.createDialog()
            val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_LOGIN_REGISTER,
                    Response.Listener<String> { response ->
                        dialog.dismissDialog()

                        try {

                            val obj = JSONObject(response)
                            val regStatus = obj.getString("reg_status");
                            if (regStatus == "ok") {
                                ClassAlertDialog(thisContext).toast("Registration successful...")

                                val userDetails = obj.getJSONArray("userDetails").getJSONObject(0)
                                saveUserDetails(userDetails)
                            } else {
                                ClassAlertDialog(thisContext).toast(regStatus)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { volleyError ->
                        dialog.dismissDialog()
                        ClassAlertDialog(thisContext).toast("ERROR IN NETWORK CONNECTION!")
                    }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["request_type"] = "st_register"
                    params["reg_no"] = stRegNo
                    params["surname"] = stSurname
                    params["firstname"] = stFirstName
                    params["othername"] = stOtherName
                    params["phone_number"] = stPhoneNo
                    params["gender"] = stGender
                    params["date_of_birth"] = stDateOfBirth
                    params["place_of_birth"] = stPlaceOfBirth
                    params["place_of_origin"] = stPlaceOfOrigin
                    params["nationality"] = stNationality
                    params["marital_status"] = stMaritalStatus
                    params["religion"] = stReligion
                    params["mode_of_entry"] = stModeOfEntry
                    params["program_type"] = stProgramType
                    params["year_of_entry"] = stYearOfEntry
                    params["award_in_view"] = stAwardInView
                    params["department"] = stDepartment
                    params["faculty"] = stFaculty
                    params["course_duration"] = stCourseDuration
                    params["next_of_kin"] = stNextOfKinName
                    params["next_of_kin_addr"] = stNextOfKinAddr
                    params["sponsor"] = stSponsorName
                    params["sponsor_addr"] = stSponsorAddr
                    return params
                }
            }

            //adding request to queue
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
            //volley interactions end
        }
    }
    private fun saveUserDetails(userDetails: JSONObject?) {
        val preference = ClassSharedPreferences(thisContext)

        val userDetailsArr = StudentRegisterClassBinder(
                userDetails?.getString("user_id"),
                userDetails?.getString("reg_no"),
                userDetails?.getString("surname"),
                userDetails?.getString("firstname"),
                userDetails?.getString("othername"),
                userDetails?.getString("phone_number"),
                userDetails?.getString("gender"),
                userDetails?.getString("date_of_birth"),
                userDetails?.getString("place_of_birth"),
                userDetails?.getString("place_of_origin"),
                userDetails?.getString("nationality"),
                userDetails?.getString("marital_status"),
                userDetails?.getString("religion"),
                userDetails?.getString("mode_of_entry"),
                userDetails?.getString("program_type"),
                userDetails?.getString("year_of_entry"),
                userDetails?.getString("award_in_view"),
                userDetails?.getString("department"),
                userDetails?.getString("faculty"),
                userDetails?.getString("course_duration"),
                userDetails?.getString("next_of_kin"),
                userDetails?.getString("next_of_kin_addr"),
                userDetails?.getString("sponsor"),
                userDetails?.getString("sponsor_addr")
        )
        preference.setUserJSONDetails(Gson().toJson(mutableListOf(userDetailsArr)))
        preference.setUserRegNo(userDetailsArr.reg_no)


        dialog!!.dismiss()
        listener?.onRegSuccessful()
    }

    private fun processLogin() {
        val stLoginRegNo = toString(st_login_regno)

        if(checkEmpty(stLoginRegNo)){
            ClassAlertDialog(thisContext).toast("Enter your reg no.")
        }else{

            //creating volley string request
            val dialog= ClassProgressDialog(context)
            dialog.createDialog()
            val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_LOGIN_REGISTER,
                    Response.Listener<String> { response ->
                        dialog.dismissDialog()

                        try {

                            val obj = JSONObject(response)
                            val regStatus = obj.getString("log_status");
                            if (regStatus == "ok") {
                                ClassAlertDialog(thisContext).toast("Login successful...")

                                val userDetails = obj.getJSONArray("userDetails").getJSONObject(0)
                                saveUserDetails(userDetails)
                            } else {
                                ClassAlertDialog(thisContext).toast(regStatus)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { volleyError ->
                        dialog.dismissDialog()
                        ClassAlertDialog(thisContext).toast("ERROR IN NETWORK CONNECTION!")
                    }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["request_type"] = "st_login"
                    params["login_reg_no"] = stLoginRegNo
                    return params
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            //adding request to queue
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
            //volley interactions end
        }
    }



    private fun initializeData(){
        genderSpinner()
        departmentSpinner()
        facultySpinner()
        nationalitySpinner()
        maritalStatusSpinner()
        religionSpinner()
        modeOfEntrySpinner()
        programTypeSpinner()
        yearOfEntrySpinner()
        courseDurationSpinner()
        awardInViewSpinner()
    }


    private fun departmentSpinner(){
        val spinnerArray = arrayOf("Department","Computer science", "Pure and applied physics", "Mathematics", "Chemistry", "Geology", "Geography")


        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_department?.adapter = subjectSpinnerArrayAdapter

        st_department?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stDepartment = spinnerArray[position]
            }

        }
    }

    private fun facultySpinner(){
        val spinnerArray = arrayOf("Faculty","Science", "Agriculture", "Education", "Social and Management sciences")


        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_faculty?.adapter = subjectSpinnerArrayAdapter

        st_faculty?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stFaculty = spinnerArray[position]
            }

        }
    }

    private fun genderSpinner(){
        val spinnerArray = arrayOf("Gender", "Male", "Female")


        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_gender?.adapter = subjectSpinnerArrayAdapter

        st_gender?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stGender = spinnerArray[position]
            }

        }
    }



    private fun nationalitySpinner(){
        val spinnerArray = arrayOf("Nationality","Nigeria", "Togo", "Cameroon", "Chad", "Benin Republic", "Ghana")

        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_nationality?.adapter = subjectSpinnerArrayAdapter

        st_nationality?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stNationality = spinnerArray[position]
            }

        }
    }

    private fun maritalStatusSpinner(){
        val spinnerArray = arrayOf("Marital Status","Single", "Married", "Unknown")

        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_marital_status?.adapter = subjectSpinnerArrayAdapter

        st_marital_status?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stMaritalStatus = spinnerArray[position]
            }

        }
    }

    private fun religionSpinner(){
        val spinnerArray = arrayOf("Religion","Christian", "Muslim", "ATR")

        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_religion?.adapter = subjectSpinnerArrayAdapter

        st_religion?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stReligion = spinnerArray[position]
            }

        }
    }
    private fun modeOfEntrySpinner(){
        val spinnerArray = arrayOf("Mode of entry","UTME", "Direct Entry")

        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_mode_of_entry?.adapter = subjectSpinnerArrayAdapter

        st_mode_of_entry?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stModeOfEntry = spinnerArray[position]
            }

        }
    }

    private fun programTypeSpinner(){
        val spinnerArray = arrayOf("Program type","Full time", "Part time")

        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_program_type?.adapter = subjectSpinnerArrayAdapter

        st_program_type?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stProgramType = spinnerArray[position]
            }

        }
    }

    private fun yearOfEntrySpinner(){
        val spinnerArray = arrayListOf<String>()
        spinnerArray.add("Year of entry")
        for (i in 2016 downTo 1993){
            spinnerArray.add("$i")
        }

        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_year_of_entry?.adapter = subjectSpinnerArrayAdapter

        st_year_of_entry?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stYearOfEntry = spinnerArray[position]
            }

        }
    }

    private fun courseDurationSpinner(){
        val spinnerArray = arrayOf("Course duration","4 years", "5 years", "6 years")


        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_course_duration?.adapter = subjectSpinnerArrayAdapter

        st_course_duration?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stCourseDuration = spinnerArray[position]
            }

        }
    }


    private fun awardInViewSpinner(){
        val spinnerArray = arrayOf("B.SC ","B.ENG", "B.TECH")


        val subjectSpinnerArrayAdapter = ArrayAdapter<String>(thisContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
        //selected item will look like a spinner set from XML
        subjectSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_award_in_view?.adapter = subjectSpinnerArrayAdapter

        st_award_in_view?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stAwardInView = spinnerArray[position]
            }

        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar)
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        return dialog
    }


    //Fragment communication with the Home Activity Starts
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentStRegisterInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement FragmentStRegisterInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface FragmentStRegisterInteractionListener {
        fun onRegSuccessful()
    }
    //Fragment communication with the Home Activity Stops
}

