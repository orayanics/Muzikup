package com.example.muzikup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import auth.SpotifyPkceLoginActivityImpl
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity


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
            //startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}