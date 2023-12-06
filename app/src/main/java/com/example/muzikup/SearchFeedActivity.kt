package com.example.muzikup

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.LastFmService
import api.SearchResponse
import api.TrackResponse
import auth.guardValidSpotifyApi
import com.adamratzman.spotify.SpotifyException
import com.example.muzikup.Track
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import data.Model
import data.Review
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
     private lateinit var database: DatabaseReference

     private var review = Review(
         reviewId = "",
         track = "",
         artist = "",
         content = "",
         likes = 0,
         isLiked = mutableMapOf(),
         username = ""
     )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // database
        database = Firebase.database.reference

        // Guard clause for valid Spotify API
        try{
            guardValidSpotifyApi(SearchFeedActivity::class.java) { api ->
//            val token = Model.credentialStore.spotifyToken
//                ?: throw SpotifyException.ReAuthenticationNeededException()
//            val usesPkceAuth = token.refreshToken != null
                if (!api.isTokenValid(true).isValid) {
                    throw SpotifyException.ReAuthenticationNeededException()
                }
                // go to the whole searchfeed
                Toast.makeText(this, "User: ${api.getUserId()}", Toast.LENGTH_SHORT).show()
                setupActivity()
            }
        } catch (e:Exception){
            Log.e("launch_spotify", e.toString())
        }

    }

     private fun setupActivity() {
         //initialize retrofit
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val key = "a863ec62a3b501170c759fc562a79267"
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        lastFmService = retrofit.create(LastFmService::class.java)

        try{
            // Initialize RecyclerView and adapters
            trackRecyclerView = findViewById(R.id.recyclerViewTracks)
            trackRecyclerView.layoutManager = LinearLayoutManager(this)
            searchAdapter = SearchAdapter(emptyList(), this)
            trackRecyclerView.adapter = searchAdapter

            // Initialize other elements
            val scrollView : ScrollView = findViewById(R.id.scrollPost)
            val btnPost : Button = findViewById(R.id.btnPost)

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

            // Post Listener
            btnPost.setOnClickListener {
                val content : EditText = findViewById(R.id.postContent)
                review.content = content.toString();
                postReview(review)
                Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("launch_spotify", e.toString())
        }

    }

     override fun onItemClick(position: Int, track: String, artist: String) {
         Toast.makeText(this, "$track $artist", Toast.LENGTH_SHORT).show()

         val info : TextView = findViewById(R.id.txtInfo)
         val content : EditText = findViewById(R.id.postContent)
         // get username

         review = Review(
             reviewId = database.push().key.toString(),
             track = track,
             artist = artist,
             content = content.toString(),
             likes = 0,
             isLiked = mutableMapOf(),
             username = ""
         )

         info.visibility = View.VISIBLE
         info.text = "$track by $artist"
         val scrollView : ScrollView = findViewById(R.id.scrollPost)
         scrollView.visibility = View.GONE
         searchAdapter.notifyItemChanged(position)
     }

     // Post to Firebase
     private fun postReview(review : Review) {
         database.child("Review").child(review.reviewId.toString()).setValue(review)
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