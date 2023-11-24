package api

import com.example.muzikup.Track
import com.google.gson.annotations.SerializedName

data class TrackMatches(
    @SerializedName("track") val track: List<Track>
)