package com.example.muzikup

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("album") val album: Album
)
