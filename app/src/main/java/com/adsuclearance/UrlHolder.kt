package com.adsuclearance

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat


object  UrlHolder{
//    private const val URL_ROOT = "http://192.168.44.236/adsu/adsu-clearance/server_request/"
//    private const val URL_ROOT = "http://192.168.43.168/news/server_request/"
    private val URL_ROOT = "http://unnelection.com.ng/adsu/adsu-clearance/server_request/"


    val URL_GET_CLEARANCE_LIST: String?  = URL_ROOT + "get_clearance_list.php"
    val URL_LOGIN_REGISTER: String?     = URL_ROOT + "login_register.php"
    val URL_ST_CLEARANCE: String?  = URL_ROOT + "st_clearance.php"


}