package com.github.pksokolowski.posty.api.models

import com.google.gson.annotations.SerializedName

class User(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String
)