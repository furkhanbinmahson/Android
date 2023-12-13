package com.example.tabletalk.presentation.data

import com.google.android.gms.maps.model.LatLng

data class Restaurants(
    var id:String? = null,
    var imageUrl:String? = null,
    var location:String? = null,
    var name:String?  = null,
    var rating:String? = null,
    var food:List<Food?>? = null
)
