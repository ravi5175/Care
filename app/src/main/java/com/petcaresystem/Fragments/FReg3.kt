package com.petcaresystem.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView

import com.petcaresystem.R
import kotlinx.android.synthetic.main.fragment_freg3.*


class FReg3 : Fragment(R.layout.fragment_freg3) {

    var petOwner : Boolean = false


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        pet_owner_toggle_update.setOnToggledListener(object: OnToggledListener{
            override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
                petOwner = isOn
            }
        })

    }


}
