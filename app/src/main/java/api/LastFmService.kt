package api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmService {
    @GET("/2.0/?method=track.getInfo")
    fun getTrack(
        @Query("api_key") key: String,
        @Query("artist") artist: String,
        @Query("track") track: String,
        @Query("format") format: String = "json"
    ): Call<TrackResponse>
}

//    Call<TrackData> groupList(@Query("api_key") key String,
//    @Query("artist") artist: String,
//    @Query("track") track: String,
//    @Query("format") format: String = "json")
