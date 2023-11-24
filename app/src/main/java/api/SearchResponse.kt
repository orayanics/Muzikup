package api

import com.example.muzikup.Track
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("results") val results: Results
)