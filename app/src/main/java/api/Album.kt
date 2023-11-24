package com.example.muzikup

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("artist") val artist: String,
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: List<Image>
)

