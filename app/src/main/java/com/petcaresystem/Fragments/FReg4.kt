package com.petcaresystem.Fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView

import com.petcaresystem.R
import kotlinx.android.synthetic.main.fragment_freg4.*


class FReg4 : Fragment(R.layout.fragment_freg4) {

    var expertToggleStatus : Boolean = false
    var ngoToggleStatus    : Boolean = false
    var ngoAmbulanceFacility  : Boolean = false
    var hospitalAmbulanceFacility : Boolean = false

    var hospitalProofId : Uri? = null
    var ngoProofId : Uri? = null

    private val GALLERY_REQUEST_CODE_EXPERT = 117
    private val GALLERY_REQUEST_CODE_NGO = 118

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        expert_toggle_listener.setOnToggledListener(object: OnToggledListener{
            override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
                if (isOn){
                    expertToggleStatus = true
                    expert_layout.visibility = View.VISIBLE
                }else{
                    expertToggleStatus = false
                    expert_layout.visibility = View.GONE
                }
            }
        })

        ngo_toggle_listener.setOnToggledListener(object: OnToggledListener{
            override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
                if (isOn){
                    ngoToggleStatus = true
                    ngo_layout.visibility = View.VISIBLE
                }else{
                    ngoToggleStatus = false
                    ngo_layout.visibility = View.GONE
                }
            }
        })

        ngo_ambulance_facility_listener.setOnToggledListener(object : OnToggledListener{
            override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
                ngoAmbulanceFacility = isOn
            }
        })

        hospital_ambulance_facility_listener.setOnToggledListener(object : OnToggledListener{
            override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
                hospitalAmbulanceFacility = isOn
            }
        })

        hospital_proof_id_upload_button.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Choose Image"),GALLERY_REQUEST_CODE_EXPERT)
        }

        ngo_proof_id_upload_button.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Choose Image"),GALLERY_REQUEST_CODE_NGO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_REQUEST_CODE_EXPERT && resultCode == RESULT_OK && data !=null){
            hospitalProofId = data.data
            hospital_proof_id.setImageURI(hospitalProofId)
        }

        if(requestCode == GALLERY_REQUEST_CODE_NGO && resultCode == RESULT_OK && data != null){
            ngoProofId = data.data
            ngo_proof_id.setImageURI(ngoProofId)
        }
    }

}
