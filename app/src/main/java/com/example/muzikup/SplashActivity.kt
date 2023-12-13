package com.example.muzikup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val video : VideoView = findViewById(R.id.splashVid)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.muzikup)
        video.setVideoURI(videoUri)
        video.start()

        video.setOnCompletionListener {
            // Replace MainActivity.class with the activity you want to navigate to after splash screen
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash screen activity
        }
    }
}