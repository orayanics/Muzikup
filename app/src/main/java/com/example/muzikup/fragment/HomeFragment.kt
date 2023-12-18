package com.example.muzikup.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.muzikup.R
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.squareup.picasso.Picasso
import data.UserProfile

class HomeFragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var profileImageView: ImageView

    private lateinit var userReviewTextView: TextView
    private lateinit var userLikeTextView: TextView
    private lateinit var userLevelTextView: TextView
    private lateinit var ProgLevelTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var expTextView: TextView

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
        userReviewTextView = view.findViewById(R.id.userReview)
        userLikeTextView = view.findViewById(R.id.userLike)
        userLevelTextView = view.findViewById(R.id.userLevel)
        ProgLevelTextView = view.findViewById(R.id.ProgLevel)
        progressBar = view.findViewById(R.id.progressBar)
        expTextView = view.findViewById(R.id.exp)

        // Retrieve the display name from SharedPreferences
        val preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userName = preferences.getString("DISPLAY_NAME", "DefaultUsername")
        val profilePictureUrl = preferences.getString("PROFILE_PICTURE_URL", "")

        // Fetch user profile data from Firebase and update UI
        fetchUserProfileFromFirebase(userName)


        //set display name
        usernameTextView.text = userName ?: "DefaultUsername"

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



    private fun fetchUserProfileFromFirebase(userName: String?) {
        if (userName != null) {
            // Get a reference to your Firebase Realtime Database
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val usersReference: DatabaseReference = database.reference.child("users")

            // Query the database to check if the username exists
            usersReference.orderByChild("displayName").equalTo(userName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Username exists, fetch the user profile data
                            val userProfile: UserProfile? = dataSnapshot.children.firstOrNull()
                                ?.getValue(UserProfile::class.java)

                            if (userProfile != null) {
                                // Update UI with fetched data
                                updateUserProfileUI(userProfile)
                                Log.d("FirebaseFetch", "Fetched user profile from the database: $userProfile")
                            } else {
                                Log.d("FirebaseFetch", "Error: User profile is null.")
                            }
                        } else {
                            // Username does not exist in the database
                            Log.d("FirebaseFetch", "Username does not exist in the database.")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("FirebaseFetch", "Error: ${databaseError.message}")
                    }
                })
        }
    }


    private fun updateUserProfileUI(userProfile: UserProfile) {
        // Update UI with user profile data
        userReviewTextView.text = userProfile.userReview.toString()
        userLikeTextView.text = userProfile.totalLikes.toString()

        // Calculate the number of level increases based on userExp
        val levelIncrease = userProfile.userExp / 100

        // If there is a level increase
        if (levelIncrease > 0) {
            // Increase the userLevel by the calculated number
            userProfile.userLevel += levelIncrease

            // Reset userExp to the remainder after reaching 100
            userProfile.userExp %= 100

            // Update userLevel in the database
            updateUserLevelInDatabase(userProfile.displayName, userProfile.userLevel)

            // Reset userExp in the database
            resetUserExpInDatabase(userProfile.displayName, userProfile.userExp)
        }

        // Calculate the total userExp based on userLike and userReview
        val userLikeExp = userProfile.totalLikes * 5
        val userReviewExp = userProfile.userReview * 20
        val totalUserExp = userLikeExp + userReviewExp

        // Check if the total userExp has changed
        if (totalUserExp != userProfile.userExp) {
            // Update userExp in the database
            updateUserExpInDatabase(userProfile.displayName, totalUserExp)

            // Update userExp in the UserProfile object
            userProfile.userExp = totalUserExp
        }

        // Make sure userLevel and userExp are consistent
        ProgLevelTextView.text = "LEVEL ${userProfile.userLevel}"
        userLevelTextView.text = userProfile.userLevel.toString()

        progressBar.progress = userProfile.userExp
        expTextView.text = "${userProfile.userExp}/100 EXP"
    }

    private fun updateUserLevelInDatabase(displayName: String, userLevel: Int) {
        // Update userLevel in the Firebase Realtime Database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersReference: DatabaseReference = database.reference.child("users")

        usersReference.orderByChild("displayName").equalTo(displayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.children.firstOrNull()?.ref?.child("userLevel")
                            ?.setValue(userLevel)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FirebaseUpdate", "Error: ${databaseError.message}")
                }
            })
    }

    private fun resetUserExpInDatabase(displayName: String, userExp: Int) {
        // Reset userExp in the Firebase Realtime Database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersReference: DatabaseReference = database.reference.child("users")

        usersReference.orderByChild("displayName").equalTo(displayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.children.firstOrNull()?.ref?.child("userExp")
                            ?.setValue(userExp)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FirebaseUpdate", "Error: ${databaseError.message}")
                }
            })
    }

    private fun updateUserExpInDatabase(displayName: String, userExp: Int) {
        // Update userExp in the Firebase Realtime Database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersReference: DatabaseReference = database.reference.child("users")

        usersReference.orderByChild("displayName").equalTo(displayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.children.firstOrNull()?.ref?.child("userExp")
                            ?.setValue(userExp)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FirebaseUpdate", "Error: ${databaseError.message}")
                }
            })
    }
}
