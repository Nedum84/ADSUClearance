package com.adsuclearance


data class StudentRegisterClassBinder(val user_id: String?,val reg_no: String?, val surname: String?, val firstname: String?,
                                      val othername: String?, val phone_number: String?, val gender: String?, val date_of_birth: String?,
                                      val place_of_birth: String?, val place_of_origin: String?,
                                      val nationality: String?, val marital_status: String?, val religion: String?,val mode_of_entry:String?,
                                      val program_type: String?, val year_of_entry: String?, val award_in_view: String?,val department:String?,
                                      val faculty: String?, val course_duration: String?, val next_of_kin: String?,val next_of_kin_addr:String?,
                                      val sponsor: String?, val sponsor_addr: String?, val st_password: String?="",val other_var:String?="")


data class StudentClearanceClassBinder(val clearance_id: Int?,val clearance_name: String, val clearance_fee: String, val clearance_receipt: String,
                                      val clearance_time: String?, val is_user_cleared: String?="0")

