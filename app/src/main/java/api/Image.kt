package com.example.muzikup

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("#text") val text: String,
    @SerializedName("size") val size: String = "extralarge"
)