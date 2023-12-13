package com.example.tabletalk.presentation.data

data class DoneRequests(
    val reqId:String? = null,
    val user1Id :String? = null,
    val user2Id:String? = null,
    val user1ImageUrl:String? = null,
    val user2ImageUrl:String? = null,
    val user1Name:String? = null,
    val user2Name:String? = null,
    val latitude:Double? = null,
    val longitude:Double? = null,
    val date:Long? = null,
    val time:String? = null,
    var isDone:Boolean? = null
)
