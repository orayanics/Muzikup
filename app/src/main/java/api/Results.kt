package api

import com.example.muzikup.Album
import com.example.muzikup.Artist
import com.google.gson.annotations.SerializedName

data class Results(
    @SerializedName("trackmatches") val trackmatches: TrackMatches
)