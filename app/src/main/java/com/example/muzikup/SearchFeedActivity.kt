package com.example.muzikup

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.LastFmService
import api.SearchResponse
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

        // Load in navbar based on role
        identifyAccess("USER")
        val btnAdd : ImageView = findViewById(R.id.btnAdd)
        btnAdd.setImageResource(R.drawable.add_fill)

        // Start Post Activity
        setupActivity()

    }

     private fun setupActivity() {
        //initialize retrofit
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        lastFmService = retrofit.create(LastFmService::class.java)

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
                val isContent = content.text.toString()
                if(isContent.isNotEmpty()){
                    review.content = isContent
                    postReview(review)
                    showToast("Review posted")
                    Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show()
                } else {
                    showToast("Review cannot be empty")
                }
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
         review.reviewId?.let { database.child("Review").child(it).setValue(review) }
     }

     fun performSearch(query: String) {
        // Perform the API search request
        val call = lastFmService.searchTracks("a863ec62a3b501170c759fc562a79267", query)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                Log.d("APIResponse", "API Link: ${response.body()}")
                Log.d("APIResponse", "Code: ${response.code()}")
                Log.d("APIRespossnse", "Body: ${response.body()}")
                if (response.isSuccessful) {

                    //validation
                    val jsonString = response.body()?.toString()
                    Log.d("JSONResponse", jsonString ?: "Response body is null")

                    val searchResponse = response.body()
                    val searchResults = searchResponse?.results?.trackmatches?.track
                    if (!searchResults.isNullOrEmpty()) {
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

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                // Handle failure
                Log.e("APIFailure", "Error: ${t.message}")
            }
        })
    }

     private fun showToast(message: String) {
         Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
     }

     private fun identifyAccess(access : String){
         val cardView : CardView = findViewById(R.id.footerId)

         val layoutResId: Int = if (access == "USER") {
             // return 1
             R.layout.footer_user
         } else {
             // return 0
             R.layout.footer_admin
         }
         // Inflate and replace the included layout
         val inflater = LayoutInflater.from(this)
         val newIncludedLayout = inflater.inflate(layoutResId, cardView, false)
         cardView.removeAllViews() // Remove the previous included layout
         cardView.addView(newIncludedLayout)
     }

 }