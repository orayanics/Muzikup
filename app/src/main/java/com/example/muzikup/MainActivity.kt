package com.example.muzikup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.muzikup.fragment.AddPostFragment
import com.example.muzikup.fragment.FeedFragment
import com.example.muzikup.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFragment(FeedFragment())
        //searchbar
        val searchView: SearchView = findViewById(R.id.btnSearch)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            val titleView: View = findViewById(R.id.title)
            val navbarSearchView: View = findViewById(R.id.navbarSearch)

            if (hasFocus) {
                // Hide the title and show the SearchView when focused
                titleView.visibility = View.GONE
                navbarSearchView.visibility = View.VISIBLE
            } else {
                // Show the title and hide the SearchView when not focused
                titleView.visibility = View.VISIBLE

                // Collapse the SearchView when not focused
                searchView.onActionViewCollapsed()
            }
        }

        // Update visibility of navbars
        findViewById<View>(R.id.navbarDefault).visibility = View.GONE
        findViewById<View>(R.id.navbarSearch).visibility = View.VISIBLE

        // ICONS
        val btnHome : ImageView = findViewById(R.id.footerHome)
        val btnAdd : ImageView = findViewById(R.id.btnAdd)
        val btnProfile : ImageView = findViewById(R.id.btnProfile)
        btnHome.setImageResource(R.drawable.home_fill)

        // NAVIGATION
        val homeBtn : ImageView = findViewById(R.id.footerHome)
        val postBtn : ImageView = findViewById(R.id.btnAdd)
        val profileBtn : ImageView = findViewById(R.id.btnProfile)

        homeBtn.setOnClickListener {
            btnHome.setImageResource(R.drawable.home_fill)
            btnAdd.setImageResource(R.drawable.add_nofill)
            btnProfile.setImageResource(R.drawable.profile_nofill)
            setFragment(FeedFragment())

            // Update visibility of navbars
            findViewById<View>(R.id.navbarDefault).visibility = View.GONE
            findViewById<View>(R.id.navbarSearch).visibility = View.VISIBLE

            //searchbar
            val searchView: SearchView = findViewById(R.id.btnSearch)
            searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                val titleView: View = findViewById(R.id.title)
                val navbarSearchView: View = findViewById(R.id.navbarSearch)

                if (hasFocus) {
                    // Hide the title and show the SearchView when focused
                    titleView.visibility = View.GONE
                    navbarSearchView.visibility = View.VISIBLE
                } else {
                    // Show the title and hide the SearchView when not focused
                    titleView.visibility = View.VISIBLE

                    // Collapse the SearchView when not focused
                    searchView.onActionViewCollapsed()
                }
            }
        }

        postBtn.setOnClickListener {
            btnAdd.setImageResource(R.drawable.add_fill)
            btnHome.setImageResource(R.drawable.home_nofill)
            btnProfile.setImageResource(R.drawable.profile_nofill)
            setFragment(AddPostFragment())

            // Update visibility of navbars
            findViewById<View>(R.id.navbarDefault).visibility = View.VISIBLE
            findViewById<View>(R.id.navbarSearch).visibility = View.GONE

        }

        profileBtn.setOnClickListener {
            btnAdd.setImageResource(R.drawable.add_nofill)
            btnHome.setImageResource(R.drawable.home_nofill)
            btnProfile.setImageResource(R.drawable.profile_fill)
            val homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit()

            // Update visibility of navbars
            findViewById<View>(R.id.navbarDefault).visibility = View.VISIBLE
            findViewById<View>(R.id.navbarSearch).visibility = View.GONE

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