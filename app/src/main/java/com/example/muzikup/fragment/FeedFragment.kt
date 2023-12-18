package com.example.muzikup.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.muzikup.FeedAdapter
import com.example.muzikup.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import data.Review

class FeedFragment : Fragment() {


    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter
    private var originalFeedResults: List<Review> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        // Initialize UI elements and set up database reference
        recyclerView = view.findViewById(R.id.feedRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        database = Firebase.database.reference

        // Retrieve and display review data
        database.child("Review").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val feedResults = mutableListOf<Review>()
                originalFeedResults = mutableListOf()

                for (snapshot in dataSnapshot.children) {
                    val feedItem = snapshot.getValue(Review::class.java)
                    feedItem?.let {
                        feedResults.add(it)
                        (originalFeedResults as MutableList<Review>).add(it) // Add to original feedResults
                    }
                }

                feedAdapter = FeedAdapter(feedResults, object : FeedAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int, review: Review) {
                        // Handle item click
                        likePost(review, "sample")
                        Log.d("RecyclerView", "Item clicked at position $position with reviewId ${review.reviewId}")
                    }
                })
                recyclerView.adapter = feedAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error getting data", databaseError.toException())
            }
        })

        // Set up SearchView
        val searchView: SearchView = activity?.findViewById(R.id.btnSearch) ?: return view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFeed(newText)
                return true
            }
        })

        return view
    }

    // Other functions

    private fun filterFeed(query: String?) {
        val filteredList = mutableListOf<Review>()

        originalFeedResults.forEach { review ->
            if (query.isNullOrBlank() || review.track?.contains(query, ignoreCase = true) == true) {
                filteredList.add(review)
            }
        }

        feedAdapter.updateList(filteredList)
    }

    // For liking
    fun likePost(review: Review, username : String) {
        val reviewRef = review.reviewId?.let { database.child("Review").child(it) }

        // Check if the user has already liked the review
        reviewRef?.child("isLiked")?.child(username)?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot.exists() && snapshot.value == true) {
                removeLike(review, username)
                Log.d("RecyclerView", "Item is unliked with reviewId ${review.reviewId}")
                // User has already liked the review
                // Handle this scenario as needed
            } else {
                // User hasn't liked the review or their like is removed

                // Add the user's like
                val updateMap = mutableMapOf<String, Any>("isLiked/$username" to true)
                reviewRef.updateChildren(updateMap)
                // Increment the 'likes' count
                reviewRef.child("likes").setValue(ServerValue.increment(1))
                Log.d("RecyclerView", "Item is liked with reviewId ${review.reviewId}")
            }
        }?.addOnFailureListener {
            // Handle failure to retrieve like status
        }
    }

    fun removeLike(review: Review, username : String) {
        val updateMap = mutableMapOf<String, Any>("$username" to false)
        val reviewRef = review.reviewId?.let { database.child("Review").child(it) }
        reviewRef?.child("isLiked")?.child(username)?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot.exists() && snapshot.value == true) {
                // Remove the user's like
                val updateMap = mutableMapOf<String, Any>("isLiked/$username" to false)
                reviewRef.updateChildren(updateMap)

                // Decrement the 'likes' count
                reviewRef.child("likes").setValue(ServerValue.increment(-1))
            } else {
                // User hasn't liked the review or their like is already removed
                // Handle this scenario as needed
            }
        }?.addOnFailureListener {
            // Handle failure to retrieve like status
        }

    }
}