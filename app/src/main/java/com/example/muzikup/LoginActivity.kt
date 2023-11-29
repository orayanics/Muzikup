package com.example.muzikup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import auth.SpotifyPkceLoginActivityImpl
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin : TextView = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            onConnectSpotifyPKCEClick()
//            val i = Intent(this, RegisterActivity::class.java)
//            startActivity(i)
//            finish()
        }
    }

    private fun onConnectSpotifyPKCEClick() {
        startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
    }
}