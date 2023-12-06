package com.example.muzikup

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val btnHome : ImageView = findViewById(R.id.footerHome)
        btnHome.setColorFilter(Color.parseColor("#40AA4A"))

        identifyAccess("USER")
    }

    // Implement to activity_post, admin_feed (DELETE), layout_profile
    fun identifyAccess(access : String){
        val cardView : CardView = findViewById(R.id.footerId)

        val userAccess = access

        val layoutResId: Int = if (userAccess == "USER") {
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
        val reviewRef = database.child("Review").child(review.reviewId.toString())

        // Check if the user has already liked the review
        reviewRef.child("isLiked").child(username).get().addOnSuccessListener { snapshot ->
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
        }.addOnFailureListener {
            // Handle failure to retrieve like status
        }
    }

    fun removeLike(review: Review, username : String) {
        val updateMap = mutableMapOf<String, Any>("$username" to false)
        val reviewRef = database.child("Review").child(review.reviewId.toString())
        reviewRef.child("isLiked").child(username).get().addOnSuccessListener { snapshot ->
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
        }.addOnFailureListener {
            // Handle failure to retrieve like status
        }

    }
}