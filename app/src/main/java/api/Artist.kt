package com.example.muzikup

import com.google.gson.annotations.SerializedName

data class Artist(
    @SerializedName("name") val name: String
)
