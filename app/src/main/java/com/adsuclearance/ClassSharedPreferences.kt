package com.adsuclearance

import android.content.Context
import java.util.HashSet

class  ClassSharedPreferences(val context: Context?){

    private val PREFERENCE_NAME = "adsu_clearance_preference"
    private val PREFERENCE_CURRENT_CLEARANCE_ID = "current_clearance_id"



    private val PREFERENCE_USER_JSON_DETAILS = "user_json_details"
    private val PREFERENCE_REG_NO = "user_reg_no"

    private val preference = context?.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!

    //set setCurrentClearanceId
    fun setCurrentClearanceDetail(data: String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_CURRENT_CLEARANCE_ID,data)
        editor.apply()
    }
    fun getCurrentClearanceDetail():String{
        return  preference.getString(PREFERENCE_CURRENT_CLEARANCE_ID,"")!!
    }




    //USER DETAILS

    //set user details arrays in JSON
    fun setUserJSONDetails(data:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_USER_JSON_DETAILS,data)
        editor.apply()
    }
    fun getUserJSONDetails():String?{
        return  preference.getString(PREFERENCE_USER_JSON_DETAILS,"")
    }
    //set UserRegNo
    fun setUserRegNo(data:String?){
        val editor = preference.edit()
        editor.putString(PREFERENCE_REG_NO,data)
        editor.apply()
    }
    //get UserRegNo
    fun getUserRegNo():String?{
        return  preference.getString(PREFERENCE_REG_NO,"")
    }
    fun isLoggedIn():Boolean{
        return getUserJSONDetails()!=""
    }

}