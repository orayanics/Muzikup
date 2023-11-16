package com.example.muzikup

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val key = "a863ec62a3b501170c759fc562a79267"
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val lastFmService = retrofit.create(LastFmService::class.java)

        try {
            val call = lastFmService.getTrack(key, "Little Mix", "Black Magic", "json")
            call.enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    // do something with the response
                    if (response.isSuccessful) {
                        // can use response.body()
                        response.body()
                        Log.i("oraya_response", response.body().toString())
                    } else {
                        // might need to inspect the error body
                        response.errorBody()
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    // handle failure
                    Log.i("oraya_error", t.toString())
                }


            })
            Log.i("oraya_response", call.toString())
            val apiResponse = URL("http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=a863ec62a3b501170c759fc562a79267&artist=cher&track=believe&format=json").readText()
        } catch (e : Exception){
            Log.e("oraya_error", e.toString())
        }

    }

//    fun appendJson(){
//        val connection = URL("http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=a863ec62a3b501170c759fc562a79267&artist=cher&track=believe&format=json").openConnection()
//        val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
//        val jsonData = StringBuilder()
//
//        var line : String?
//        while (reader.readLine().also { line = it } != null) {
//            jsonData.append(line)
//        }
//        reader.close()
//        Log.i("amado", jsonData.toString())
//    }
}