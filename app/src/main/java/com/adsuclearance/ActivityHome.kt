package com.adsuclearance

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_activity_home.*
import kotlinx.android.synthetic.main.content_activity_home.*
import org.json.JSONException
import org.json.JSONObject



class ActivityHome : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
        , FragmentDialogStudentRegister.FragmentStRegisterInteractionListener, StudentClearanceAdapter.StudentClearanceAdapterCallbackInterface
        , FragmentDialogStudentClearance.FragmentStClearanceInteractionListener {
    lateinit var prefs: ClassSharedPreferences
    lateinit var thisContext: Activity
    lateinit var curStudentDetail:StudentRegisterClassBinder

    private var clrList = mutableListOf<StudentClearanceClassBinder>()

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var ADAPTER : StudentClearanceAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        thisContext = this
        prefs = ClassSharedPreferences(thisContext)

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        val header = navigationView.getHeaderView(0)
        val navUserName = header.findViewById(R.id.nav_user_name) as TextView
        val navUserEmail = header.findViewById(R.id.nav_user_email) as TextView
        val navImageView = header.findViewById(R.id.nav_image_view) as ImageView

        if (!prefs.isLoggedIn()){
            navUserName.text = getString(R.string.app_name)
            navUserEmail.text = getString(R.string.nav_header_subtitle)

        }else{
            val stDetails = ClassSharedPreferences(thisContext).getUserJSONDetails()
            try {
                curStudentDetail = Gson().fromJson(stDetails, Array<StudentRegisterClassBinder>::class.java).asList()[0]

                navUserName.text = "${curStudentDetail.surname?.toUpperCase()}  ${curStudentDetail.firstname}"
                navUserEmail.text = curStudentDetail.reg_no?.toUpperCase()
            } catch (e: Exception) {
            }
        }
        nav_view.setCheckedItem(R.id.nav_home)//For nav view indicator



        linearLayoutManager = LinearLayoutManager(thisContext)
        ADAPTER = StudentClearanceAdapter(clrList,thisContext)
        clearance_list_recyclerview.layoutManager = linearLayoutManager
        clearance_list_recyclerview.itemAnimator = DefaultItemAnimator()
        clearance_list_recyclerview.adapter = ADAPTER
        loadClearanceList()



        refreshPage.setOnClickListener {
            refreshList()
        }
        tapToRetry.setOnClickListener {
            refreshList()
        }



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_home)//For nav view indicator
    }
    private fun refreshList(){
        clrList.clear()
        ADAPTER.addItems(clrList)
        ADAPTER.notifyDataSetChanged()
        loadClearanceList()
    }
    private fun loadClearanceList(){
        //creating volley string request
        loadingProgressbar?.visibility = View.VISIBLE
        no_network_tag?.visibility = View.GONE
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_GET_CLEARANCE_LIST,
                Response.Listener<String> { response ->
                    loadingProgressbar?.visibility = View.GONE

                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            val jsonResponse = obj.getJSONArray("clearance_arrayrsz")

                            if ((jsonResponse.length()!=0)){

                                val qDataArray = mutableListOf<StudentClearanceClassBinder>()
                                for (i in 0 until jsonResponse.length()) {
                                    val jsonObj = jsonResponse.getJSONObject(i)
                                    val subject = StudentClearanceClassBinder(
                                            jsonObj.getInt("clearance_id"),
                                            jsonObj.getString("clearance_name"),
                                            jsonObj.getString("clearance_fee"),
                                            jsonObj.getString("clearance_receipt"),
                                            jsonObj.getString("clearance_time"),
                                            jsonObj.getString("is_user_cleared")
                                    )
                                    if (subject !in clrList)qDataArray.add(subject)
                                }
                                ADAPTER.addItems(qDataArray)

                            }else{
                                ClassAlertDialog(thisContext).toast("No data found...")
                            }
                        } else {
                            ClassAlertDialog(thisContext).toast("An error occurred, try again...")
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { _ ->
                    loadingProgressbar?.visibility = View.GONE
                    no_network_tag?.visibility = View.VISIBLE
                    ClassAlertDialog(thisContext).toast("No Network Connection...")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "get_clearance_list"
                params["st_reg_no"] = prefs.getUserRegNo()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_profile -> {
                if(!prefs.isLoggedIn()){
                    ClassAlertDialog(thisContext).toast("You have to register/login to view your profile...")
                }else{
                    startActivity(Intent(this, ActivityMyProfile::class.java))
                }
            }
            R.id.action_logout->{
                if(!prefs.isLoggedIn()){
                    ClassAlertDialog(thisContext).toast("Not logged IN...")
                }else{
                    prefs.setUserJSONDetails("")
                    this.onRegSuccessful()//reload the activity
                    ClassAlertDialog(thisContext).toast("You have logged out successfully...")
                }
            }
            R.id.action_about_us->{
                aboutUsDialog()
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {

            }
            R.id.nav_login -> {
                if(prefs.isLoggedIn()){
                    ClassAlertDialog(thisContext).toast("You have already logged IN...")
                }else{
                    stRegisterDialogShow()
                }
            }
            R.id.nav_profile -> {
                if(!prefs.isLoggedIn()){
                    ClassAlertDialog(thisContext).toast("You have to register/login to view your profile...")
                }else{
                    startActivity(Intent(this, ActivityMyProfile::class.java))
                }
            }
            R.id.nav_share -> {
                ClassShareApp(thisContext).shareApp()
            }
            R.id.nav_about_us->{
                aboutUsDialog()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun aboutUsDialog() {
        ClassAlertDialog(thisContext).alertMessage("This is ADSU final year online clearance....." +
                "\n\nDeveloped by Android/Web enthusiast. We develop robust, dynamic & responsible applications ","About Us")
    }


    override fun onRegSuccessful() {
        finish()
        startActivity(intent)
    }

    override fun onSelClearanceDialog(studentClearanceClassBinder: StudentClearanceClassBinder) {
        if(!prefs.isLoggedIn()){
            AlertDialog.Builder(this)
                    .setTitle("Login Required!")
                    .setMessage("You need to login inorder to complete your clearance")
                    .setPositiveButton("Login"
                    ) { _, _ ->
                        stRegisterDialogShow()

                    }.setNegativeButton("Cancel"
                    ) { _, _ ->

                    }
                    .show()
            return
        }else{
            stClearanceDialogShow()
        }
    }

    override fun onClearanceSuccessful() {
        refreshList()
    }




    //student register
    private val dialogFragmentStRegister = FragmentDialogStudentRegister()
    private fun stRegisterDialogShow(){
        if(dialogFragmentStRegister.isAdded)return

        val ft = supportFragmentManager.beginTransaction()
//        val prev = supportFragmentManager.findFragmentByTag("dialog")
        val prev = supportFragmentManager.findFragmentByTag(FragmentDialogStudentRegister::class.java.name)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragmentStRegister.show(ft, FragmentDialogStudentRegister::class.java.name)
    }


    //student clearance
    private val dialogFragmentStudentClearance = FragmentDialogStudentClearance()
    private fun stClearanceDialogShow(){
        if(dialogFragmentStudentClearance.isAdded)return

        val ft = supportFragmentManager.beginTransaction()
//        val prev = supportFragmentManager.findFragmentByTag("dialog")
        val prev = supportFragmentManager.findFragmentByTag(FragmentDialogStudentClearance::class.java.name)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragmentStudentClearance.show(ft, FragmentDialogStudentClearance::class.java.name)
    }

}
