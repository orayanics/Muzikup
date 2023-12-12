package com.example.muzikup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.muzikup.fragment.AddPostFragment
import com.example.muzikup.fragment.FeedFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFragment(FeedFragment())

        // ICONS
        val btnHome : ImageView = findViewById(R.id.footerHome)
        val btnAdd : ImageView = findViewById(R.id.btnAdd)
        btnHome.setImageResource(R.drawable.home_fill)

        // NAVIGATION
        val homeBtn : ImageView = findViewById(R.id.footerHome)
        val postBtn : ImageView = findViewById(R.id.btnAdd)

        homeBtn.setOnClickListener {
            btnHome.setImageResource(R.drawable.home_fill)
            btnAdd.setImageResource(R.drawable.add_nofill)
            setFragment(FeedFragment())
        }

        postBtn.setOnClickListener {
            btnAdd.setImageResource(R.drawable.add_fill)
            btnHome.setImageResource(R.drawable.home_nofill)
            setFragment(AddPostFragment())

        }
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the fragment container with the new fragment
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)

        // Add the transaction to the back stack (optional)
        fragmentTransaction.addToBackStack(null)

        // Commit the transaction
        fragmentTransaction.commit()
    }
}