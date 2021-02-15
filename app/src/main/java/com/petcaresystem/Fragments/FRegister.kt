package com.petcaresystem.Fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.petcaresystem.R
import io.grpc.okhttp.internal.Platform
import kotlinx.android.synthetic.main.fragment_fregister.*
import java.lang.Thread.sleep


class FRegister : Fragment(R.layout.fragment_fregister) {

    val userName = user_name_register_text
    val userEmail = email_register_text
    val phoneNumber = phone_number_register_text
    val passWord = password_register_text
    val confirmPassword = confirm_password_register_text
    val ccP = ccp

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
    fun sendOTP(){
        if(progress_tab.visibility == View.GONE){
            progress_tab_indeterminate_bar.visibility == View.VISIBLE
            progress_tab_indeterminate_text.visibility==View.VISIBLE
            progress_tab_indeterminate_text.setText("sending OTP...")
            progress_tab.visibility = View.VISIBLE
        }else{
            progress_tab_indeterminate_text.setText("OTP sent...")
        }
    }

    fun SomethingWrong(){
        progress_tab_indeterminate_bar.visibility = View.GONE
        progress_tab_indeterminate_text.textSize = 15F
        progress_tab_indeterminate_text.setText("oops!! something went wrong, please retry later")
    }

    fun progressText(msg :String){
        progress_tab_indeterminate_bar.visibility = View.GONE
        progress_tab_indeterminate_text.visibility = View.VISIBLE
        progress_tab.visibility = View.VISIBLE
        progress_tab_indeterminate_text.text = msg


        //progress_tab_indeterminate_text.visibility = View.GONE
       //progress_tab.visibility = View.GONE
    }
    fun closeProgress(){
        progress_tab.visibility = View.GONE
    }
}
