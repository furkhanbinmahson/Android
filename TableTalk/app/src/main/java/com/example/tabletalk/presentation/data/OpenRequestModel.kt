package com.example.tabletalk.presentation.data

import com.google.android.gms.maps.model.LatLng

data class OpenRequestModel(
    val reqId:String? = null,
    val userId:String? = null,
    val name:String? = null,
    val profileUrl:String? = null,
    val time:String? = null,
    val date:Long? = null,
    val description:String? = null,
    val latitude:Double? = null,
    val longitude:Double? = null,
    val foodPreferences:List<String>? = null,
    var isWaitingForConfirmation:Boolean? = null,
    var waiterId:String? = null,
    var waiterName:String? = null,
    var waiterProfileUrl:String? = null
)
