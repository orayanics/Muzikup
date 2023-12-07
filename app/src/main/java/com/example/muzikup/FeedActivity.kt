package com.example.muzikup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import data.Review


class FeedActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        identifyAccess("USER")
        val btnHome : ImageView = findViewById(R.id.footerHome)
        btnHome.setImageResource(R.drawable.home_fill)

        try {
            recyclerView = findViewById(R.id.feedRecycler)
            recyclerView.layoutManager = LinearLayoutManager(this)
            database = Firebase.database.reference

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val feedResults = mutableListOf<Review>()
                    for (snapshot in dataSnapshot.children) {
                        val feedItem = snapshot.getValue(Review::class.java)
                        feedItem?.let { feedResults.add(it) }
                    }
                    feedAdapter = FeedAdapter(feedResults)
                    recyclerView.adapter = feedAdapter
                }


                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error getting data", databaseError.toException())
                }
            })
        } catch (e : Exception){
            Log.e("Firebase", e.toString())
        }

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

    // For liking
    fun likePost(review: Review, username : String) {
        val reviewRef = review.reviewId?.let { database.child("Review").child(it) }

        // Check if the user has already liked the review
        reviewRef?.child("isLiked")?.child(username)?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot.exists() && snapshot.value == true) {
                // User has already liked the review
                // Handle this scenario as needed
            } else {
                // User hasn't liked the review or their like is removed

                // Add the user's like
                val updateMap = mutableMapOf<String, Any>("isLiked/$username" to true)
                reviewRef.updateChildren(updateMap)

                // Increment the 'likes' count
                reviewRef.child("likes").setValue(ServerValue.increment(1))
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