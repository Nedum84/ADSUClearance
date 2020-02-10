package com.adsuclearance


import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.alert_dialog_payment_with_atm.view.*
import kotlinx.android.synthetic.main.fragment_dialog_student_clearance.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class FragmentDialogStudentClearance: DialogFragment(){
    lateinit var thisContext: Activity
    private var listener: FragmentStClearanceInteractionListener? = null
    lateinit var curClearanceDetail:StudentClearanceClassBinder




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_student_clearance, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = activity!!

        closeDialogFrag.setOnClickListener {
            dialog!!.dismiss()
        }
        cancel_dialog.setOnClickListener {
            dialog!!.dismiss()
        }

        pay_with_atm_and_clear.setOnClickListener {
            processRegistration()
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
        if(curClearanceDetail.is_user_cleared!="0"){
            ClassAlertDialog(thisContext).toast("You have already done this clearance")
        }else{
            if (curClearanceDetail.clearance_fee.toInt()>0){
                paymentDialog()
            }else{
                AlertDialog.Builder(thisContext)
                        .setTitle("Proceed to clearance?")
                        .setPositiveButton("Proceed"
                        ) { _, _ ->
                            paymentDialog()

                        }.setNegativeButton("Cancel"
                        ) { _, _ ->

                        }
                        .show()
            }
        }
    }

    var paymentAlertDialog:AlertDialog? = null
    private fun paymentDialog(){
        val inflater = LayoutInflater.from(thisContext).inflate(R.layout.alert_dialog_payment_with_atm, null)
        val builder = AlertDialog.Builder(thisContext)

        builder.setView(inflater)
        paymentAlertDialog = builder.create()
        paymentAlertDialog?.show()

        inflater.payment_title.text = ClassHtmlFormater().fromHtml("ADSU final year <u><b>${curClearanceDetail.clearance_name}</b></u> Clearance payment")
        inflater.clearance_fee.text = "NGN ${curClearanceDetail.clearance_fee}.00"
        inflater.clearance_fee2.text = "Pay NGN ${curClearanceDetail.clearance_fee}"
        inflater.button_perform_local_transaction.setOnClickListener {
            if (toString(inflater.edit_card_number).isEmpty()){
                ClassAlertDialog(thisContext).toast("Enter the card number...")
            }else{
                makePayment()
            }
        }
    }
    private fun makePayment(){
        //creating volley string request
        val pDialog= ClassProgressDialog(context)
        pDialog.createDialog()
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_ST_CLEARANCE,
                Response.Listener<String> { response ->
                    pDialog.dismissDialog()

                    try {

                        val obj = JSONObject(response)
                        val clrStatus = obj.getString("clr_status");
                        if (clrStatus == "ok") {
                            ClassAlertDialog(thisContext).toast("Clearance successful...")

                            paymentAlertDialog?.dismiss()
                            listener?.onClearanceSuccessful()
                            dialog!!.dismiss()
                        } else {
                            ClassAlertDialog(thisContext).toast(clrStatus)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    pDialog.dismissDialog()
                    ClassAlertDialog(thisContext).toast("ERROR IN NETWORK CONNECTION!")
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["request_type"] = "st_clearance"
                params["reg_no"] = ClassSharedPreferences(thisContext).getUserRegNo()!!
                params["clearance_id"] = curClearanceDetail.clearance_id!!.toString()
                return params
            }
        }

        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
        //volley interactions end
    }



    @SuppressLint("SetTextI18n")
    private fun initializeData(){
        val clrDetails = ClassSharedPreferences(thisContext).getCurrentClearanceDetail()
        curClearanceDetail = Gson().fromJson(clrDetails, Array<StudentClearanceClassBinder>::class.java).asList()[0]

        clearance_name.text = curClearanceDetail.clearance_name
        clearance_fee.text = "₦${curClearanceDetail.clearance_fee}"


        if(curClearanceDetail.is_user_cleared=="0"){
            clearance_status.text = "Uncleared"
        }else{
            clearance_status.text = "Cleared"
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
        dialog.window!!.attributes.windowAnimations = R.style.Animation_WindowSlideUpDown
        isCancelable = false
        return dialog
    }



    //Fragment communication with the Home Activity Starts
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentStClearanceInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement FragmentStRegisterInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface FragmentStClearanceInteractionListener {
        fun onClearanceSuccessful()
    }
    //Fragment communication with the Home Activity Stops
}

