package com.example.muzikup.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.muzikup.R
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        usernameTextView = view.findViewById(R.id.usernamee)
        profileImageView = view.findViewById(R.id.profile)

        // Retrieve the display name from SharedPreferences
        val preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userName = preferences.getString("DISPLAY_NAME", "DefaultUsername")
        val profilePictureUrl = preferences.getString("PROFILE_PICTURE_URL", "")


        //set display name
        usernameTextView.text = userName

        if (profilePictureUrl?.isNotEmpty() == true) {
            Picasso.get()
                .load(profilePictureUrl)
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image if the URL is empty
                .error(R.drawable.ic_launcher_foreground) // Error image if loading fails
                .into(profileImageView)
        } else {
            // If the URL is empty, set a default profile image
            profileImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        return view
    }
}


