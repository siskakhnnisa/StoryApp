package com.example.storyappfix.response

import com.google.gson.annotations.SerializedName

data class ResultLogin(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null
)