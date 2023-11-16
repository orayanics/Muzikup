package com.example.muzikup

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("track") val track: Track
)

