package com.example.tabletalk.presentation.data

data class User(
    val userId:String? = null,
    val name:String? = null,
    val profileImageUrl:String? = null,
    val bio:String? = null,
    val foodPreferences: List<String>? = null
)
