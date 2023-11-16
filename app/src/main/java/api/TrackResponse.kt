package api

import com.example.muzikup.Track
import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("track") val track: Track
)

