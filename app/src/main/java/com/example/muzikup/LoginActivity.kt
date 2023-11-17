package com.example.muzikup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin : TextView = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}