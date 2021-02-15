package com.petcaresystem.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.petcaresystem.R
import kotlinx.android.synthetic.main.info_window.view.*

class InfoWindowAdapter(val context : Context,bitmap:Bitmap,description: String) : GoogleMap.InfoWindowAdapter {
    private var infoWindowView : View
    private var image : Bitmap? = null
    private var desc : String? = null


    init{
        infoWindowView = LayoutInflater.from(context).inflate(R.layout.info_window,null)
        image = bitmap
        desc = description
    }

    private fun renderInfoWindow(marker: Marker,view: View,image : Bitmap?,description : String?){
        view.info_window_image.setImageBitmap(image)
        view.info_window_description.setText(desc)

    }

    override fun getInfoContents(p0: Marker?): View {
        renderInfoWindow(p0!!,infoWindowView,image,desc)
        return infoWindowView
    }

    override fun getInfoWindow(p0: Marker?): View {
        renderInfoWindow(p0!!,infoWindowView,image,desc)
        return infoWindowView
    }
}