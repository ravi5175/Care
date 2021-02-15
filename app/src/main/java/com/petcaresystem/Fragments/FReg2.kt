package com.petcaresystem.Fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.petcaresystem.R
import kotlinx.android.synthetic.main.fragment_freg2.*

class FReg2 : Fragment(R.layout.fragment_freg2) {

    private val GALLERY_REQUEST_CODE = 101
    var profileAvatarUpdate : Uri? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profile_avatar_update_button.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Choose Image"),GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            profileAvatarUpdate = data.data
            profile_avatar_update.setImageURI(profileAvatarUpdate)
        }
    }



}
