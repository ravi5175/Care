package com.petcaresystem.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.petcaresystem.R
import kotlinx.android.synthetic.main.fragment_flogin.*
import kotlinx.android.synthetic.main.fragment_fregister.*

class FLogin : Fragment(R.layout.fragment_flogin) {



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun loginProgressTab(msg : String,status : Boolean,error:String){
        if (error==""){
            if (status){
                if(progress_tab_login.visibility==View.GONE){
                    progress_tab_indeterminate_bar_login.visibility = View.VISIBLE
                    progress_tab_indeterminate_text_login.visibility = View.VISIBLE
                    progress_tab_indeterminate_text_login.text = msg
                    progress_tab_login.visibility = View.VISIBLE
                }else{
                    progress_tab_indeterminate_text_login.text = msg
                }
            }else{
                progress_tab_login.visibility = View.GONE
            }
        }else{
            if(progress_tab_login.visibility==View.GONE){
                progress_tab_indeterminate_bar_login.visibility = View.GONE
                progress_tab_indeterminate_text_login.visibility = View.VISIBLE
                progress_tab_indeterminate_text_login.text = msg
                progress_tab_login.visibility = View.VISIBLE
            }else{
                progress_tab_indeterminate_bar_login.visibility = View.GONE
                progress_tab_indeterminate_text_login.text = msg
            }
        }


    }
}
