package com.example.muzikup.fragment

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.LastFmService
import api.SearchResponse
import com.adamratzman.spotify.utils.Language
import com.example.muzikup.R
import com.example.muzikup.SearchAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import data.Review
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddPostFragment : Fragment(), SearchAdapter.OnItemClickListener {

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        database = Firebase.database.reference
        setupActivity(view)
        return view
    }

    // Other Functions here
    private fun setupActivity(view: View) {
        // Initialize Retrofit
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        lastFmService = retrofit.create(LastFmService::class.java)

        // Initialize RecyclerView and adapters
        trackRecyclerView = view.findViewById(R.id.recyclerViewTracks)
        trackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SearchAdapter(emptyList(), this)
        trackRecyclerView.adapter = searchAdapter

        // Initialize other elements
        val scrollView: ScrollView = view.findViewById(R.id.scrollPost)
        val btnPost: Button = view.findViewById(R.id.btnPost)

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchViewPost)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission
                if (!query.isNullOrBlank()) {
                    performSearch(query)
                    scrollView.visibility = View.VISIBLE
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes if needed
                if (newText.isNullOrBlank()) {
                    scrollView.visibility = View.GONE
                    searchAdapter.updateData(emptyList())
                }
                return true
            }
        })

        scrollView.visibility = View.GONE

        // Post Listener
        btnPost.setOnClickListener {
            val content: EditText = view.findViewById(R.id.postContent)
            val isContent = content.text.toString()
            if (isContent.isNotEmpty()) {
                review.content = isContent
                postReview(review)
                showToast("Review posted")
                Toast.makeText(requireContext(), "Review posted", Toast.LENGTH_SHORT).show()
                navigateToFragment()
            } else {
                showToast("Review cannot be empty")
            }
        }
    }

    private fun navigateToFragment() {
        val fragmentB = FeedFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        // Replace the current fragment with FragmentB
        transaction.replace(R.id.fragmentContainer, fragmentB)
        transaction.addToBackStack(null) // Optional: Adds the transaction to the back stack
        transaction.commit()
    }

    override fun onItemClick(position: Int, track: String, artist: String) {
        try {
            Toast.makeText(requireContext(), "$track $artist", Toast.LENGTH_SHORT).show()

            val info: TextView = requireView().findViewById(R.id.txtInfo)
            val content: EditText = requireView().findViewById(R.id.postContent)

            // get username
            retrieveLastFmImage(track, artist) { imageUrl ->
            review = Review(
                reviewId = database.push().key.toString(),
                track = track,
                artist = artist,
                content = content.text.toString(),  // Use text property directly
                likes = 0,
                isLiked = mutableMapOf(),
                username = "",
                image = imageUrl
            )

            info.visibility = View.VISIBLE
            info.text = "$track by $artist"
            val scrollView: ScrollView = requireView().findViewById(R.id.scrollPost)
            scrollView.visibility = View.GONE

            searchAdapter.notifyItemChanged(position)
        }
        } catch (e : Exception){
            Log.e("RecyclerView", e.toString())
        }

    }

    // Post to Firebase
    private fun postReview(review: Review) {
        review.reviewId?.let { database.child("Review").child(it).setValue(review) }
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

                    // validation
                    val jsonString = response.body()?.toString()
                    Log.d("JSONResponse", jsonString ?: "Response body is null")

                    val searchResponse = response.body()
                    val searchResults = searchResponse?.results?.trackmatches?.track
                    if (!searchResults.isNullOrEmpty()) {
                        // Update the adapter with the received search results
                        searchAdapter.updateData(searchResults)
                        Log.d("SearchResults", "Number of results: ${searchResults.size}")
                    } else {
                        // Handle the case when the search results are empty
                        Log.d("SearchResults", "No results found for the given query.")
                        showToast("No results found for the given query.")
                    }
                } else {
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun retrieveLastFmImage(track: String, artist: String, callback: (String) -> Unit) {
        // Perform the API request to get the image URL
        val call = lastFmService.searchTracks("a863ec62a3b501170c759fc562a79267", "$track $artist")

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResults = response.body()?.results?.trackmatches?.track
                    if (!searchResults.isNullOrEmpty()) {
                        // Use the first result's image URL
                        val imageUrl = searchResults[0].images?.find { it.size == "extralarge" }?.text ?: ""
                        callback.invoke(imageUrl)
                    } else {
                        // Handle the case when there are no search results
                        callback.invoke("")
                    }
                } else {
                    // Handle error
                    Log.e("APIResponse", "Error: ${response.code()} ${response.message()}")
                    callback.invoke("")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                // Handle failure
                Log.e("APIFailure", "Error: ${t.message}")
                callback.invoke("")
            }
        })
    }

}