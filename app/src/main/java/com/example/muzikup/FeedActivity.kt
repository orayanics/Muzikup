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
}