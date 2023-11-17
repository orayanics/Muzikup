package com.example.muzikup

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val btnHome : ImageView = findViewById(R.id.footerHome)
        btnHome.setColorFilter(Color.parseColor("#40AA4A"))
    }
}