package com.example.tabletalk.presentation.data

import com.google.android.gms.maps.model.LatLng

data class Restaurants(
    val id:String? = null,
    val imageUrl:String? = null,
    val location:LatLng? = null,
    val name:String?  = null,
    val rating:String? = null,
    val foodList:List<String>? = null
)
