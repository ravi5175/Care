package com.petcaresystem.Models

import android.graphics.Bitmap

data class InfoWindowCache(
    val lat   : Int     = 0,
    val long  : Int     = 0,
    val image : Bitmap? = null,
    val desc  : String  = "default"
)