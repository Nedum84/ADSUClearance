package com.adsuclearance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.adapter_design_clearance_list.view.*



class StudentClearanceAdapter(items:MutableList<StudentClearanceClassBinder>, ctx: Context): RecyclerView.Adapter<StudentClearanceAdapter.ViewHolder>(){
    private var adapterCallbackInterface: StudentClearanceAdapterCallbackInterface? = null
    private var list = items
    private var context = ctx
    var cl_sn = 1




    init {
        try {
            adapterCallbackInterface = context as StudentClearanceAdapterCallbackInterface
        } catch (e: ClassCastException) {
            throw RuntimeException(context.toString() + "Activity must implement StudentClearanceAdapterCallbackInterface.", e)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.adapter_design_clearance_list,
                parent,
                false
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addItems(items: MutableList<StudentClearanceClassBinder>) {
        val lastPos = list.size - 1
        list.addAll(items)
        notifyItemRangeInserted(lastPos, items.size)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listDetails = list[position]

        holder.clearanceName.text     = ClassHtmlFormater().fromHtml(listDetails.clearance_name)
        holder.clearancePrice.text = "Price: ₦${listDetails.clearance_fee}"
        holder.clearanceSn.text     = "${listDetails.clearance_id}"
//        holder.clearanceSn.text     = "$cl_sn"
        cl_sn++

        if(listDetails.is_user_cleared!="0"&&ClassSharedPreferences(context).isLoggedIn()){
            holder.clearanceStatus.text = "Status: Cleared"
            holder.clearanceSn.setBackgroundResource(R.drawable.design_circle_border1)
        }else{
            holder.clearanceStatus.text = "Status: Uncleared"
            holder.clearanceSn.setBackgroundResource(R.drawable.design_circle_border2)
        }

        holder.clearanceWrapper.setOnClickListener {
            ClassSharedPreferences(context).setCurrentClearanceDetail(Gson().toJson(mutableListOf(listDetails)))

            adapterCallbackInterface?.onSelClearanceDialog(listDetails)
        }

    }


    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val clearanceWrapper = v.clearanceWrapper!!
        val clearanceName = v.clearanceName!!
        val clearanceStatus = v.clearanceStatus!!
        val clearancePrice = v.clearancePrice!!
        val clearanceSn = v.clearanceSn!!
    }



    //interface declaration
    interface StudentClearanceAdapterCallbackInterface {
        fun onSelClearanceDialog(studentClearanceClassBinder: StudentClearanceClassBinder)
    }
}
