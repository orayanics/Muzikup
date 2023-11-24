package com.example.muzikup

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.LastFmService
import api.SearchResponse
import api.TrackResponse
import com.example.muzikup.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFeedActivity : AppCompatActivity() {

    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchView: SearchView
    private lateinit var lastFmService: LastFmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_feed)

        //initialize retrofit
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val key = "a863ec62a3b501170c759fc562a79267"
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        lastFmService = retrofit.create(LastFmService::class.java)


        // Initialize RecyclerView and adapters
        trackRecyclerView = findViewById(R.id.recyclerViewTracks)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter(emptyList())
        trackRecyclerView.adapter = searchAdapter


        // Initialize SearchView
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SearchQuery", "Query submitted: $query")
                if (!query.isNullOrBlank()) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes if needed
                return true
            }
        })
    }

    fun performSearch(query: String) {
        // Perform the API search request
        val call = lastFmService.searchTracks("a863ec62a3b501170c759fc562a79267", query)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                Log.d("APIResponse", "Code: ${response.code()}")
                Log.d("APIResponse", "Body: ${response.body()}")
                if (response.isSuccessful) {

                    //validation
                    val jsonString = response.body()?.toString()
                    Log.d("JSONResponse", jsonString ?: "Response body is null")


                    val searchResponse = response.body()
                    val searchResults = searchResponse?.results?.trackmatches?.track
                    if (searchResults != null && searchResults.isNotEmpty()) {

                        // Update the adapter with the received search results
                        searchAdapter.updateData(searchResults)
                        Log.d("SearchResults", "Number of results: ${searchResults.size}")
                    }
                    else {
                        // Handle the case when the search results are empty
                        Log.d("SearchResults", "No results found for the given query.")
                        showToast("No results found for the given query.")
                    }
                }
                else {
                    // Handle error
                    Log.e("APIResponse", "Error: ${response.code()} ${response.message()}")
                    showToast("Error fetching search results.")
                }
            }

            private fun showToast(message: String) {
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                // Handle failure
                Log.e("APIFailure", "Error: ${t.message}")
            }
        })
    }
}