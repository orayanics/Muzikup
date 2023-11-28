package com.example.muzikup

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ScrollView
import android.widget.TextView
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

 class SearchFeedActivity : AppCompatActivity(), SearchAdapter.OnItemClickListener{
     private lateinit var trackRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchView: SearchView
    private lateinit var lastFmService: LastFmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

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
        try{
            trackRecyclerView = findViewById(R.id.recyclerViewTracks)
            trackRecyclerView.layoutManager = LinearLayoutManager(this)
            searchAdapter = SearchAdapter(emptyList(), this)
            trackRecyclerView.adapter = searchAdapter
            val scrollView : ScrollView = findViewById(R.id.scrollPost)
            // Initialize SearchView
            searchView = findViewById(R.id.searchViewPost)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("SearchQuery", "Query submitted: $query")
                    if (!query.isNullOrBlank()) {
                        performSearch(query)
                        scrollView.visibility = View.VISIBLE
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Handle text changes if needed
                    if(newText.isNullOrBlank()){
                        scrollView.visibility = View.GONE
                        searchAdapter.updateData(emptyList())
                    }
                    return true
                }
            })

            scrollView.visibility = View.GONE

        } catch (e: Exception) {
            Log.e("launch", e.toString())
        }

    }

     override fun onItemClick(position: Int, track: String, artist: String) {
         Toast.makeText(this, "$track $artist", Toast.LENGTH_SHORT).show()
         val info : TextView = findViewById(R.id.txtInfo)
         info.visibility = View.VISIBLE
         info.text = "$track by $artist"
         val scrollView : ScrollView = findViewById(R.id.scrollPost)
         scrollView.visibility = View.GONE
         searchAdapter.notifyItemChanged(position)
     }

     fun performSearch(query: String) {
        // Perform the API search request
        val call = lastFmService.searchTracks("a863ec62a3b501170c759fc562a79267", query)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                Log.d("APIResponse", "API Link: ${response.body()}")
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