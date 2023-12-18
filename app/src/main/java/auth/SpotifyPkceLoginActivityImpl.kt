package auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.adamratzman.spotify.models.SpotifyUserInformation
import com.example.muzikup.BuildConfig
import com.example.muzikup.MainActivity
import com.example.muzikup.SpotifyPlaygroundApplication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import data.Review
import data.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import utils.toast

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {
    override val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    override val scopes = (SpotifyScope.entries).toList()
    //override val scopes = SpotifyScope.values().toList()


    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as SpotifyPlaygroundApplication).model
        model.credentialStore.setSpotifyApi(api)
        val classBackTo = pkceClassBackTo ?: MainActivity::class.java
        pkceClassBackTo = null
        toast("Authentication complete!")
        startActivity(Intent(this, classBackTo))

        // Create a CoroutineScope
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            getId(api)

        }

    }
    private suspend fun getId(api: SpotifyClientApi) {
        try {

            //Fetch user's private information (including email and profile picture)
            val userPrivate: SpotifyUserInformation = api.users.getClientProfile()
            val userName = userPrivate.displayName
            val images = userPrivate.images

            // Use the second URL from the list (if available)
            val profilePictureUrl = if (images.size >= 2) {
                images[1].url
            } else {
                // If there is no second URL, use the first one as a fallback
                images.firstOrNull()?.url ?: ""
            }

            // Save the display name to SharedPreferences
            val preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            preferences.edit().putString("DISPLAY_NAME", userName).apply()
            preferences.edit().putString("PROFILE_PICTURE_URL", profilePictureUrl).apply()

//            // Create a UserProfile object
//            val userProfile = userName?.let { UserProfile(it) }
//
//
//            // Save the UserProfile to Firebase Realtime Database
//            if (userProfile != null) {
//                saveUserProfileToFirebase(userProfile)
//            }
//
//            // Fetch user profile data from Firebase after saving it
//            if (userName != null) {
//                fetchUserProfileFromFirebase(userName)
//            }

            // Check if the user already exists in the database
            if (userName != null) {
                checkIfUserExistsInDatabase(userName)
            }



            Log.d("login", "User's ID: $userName, Prof: $profilePictureUrl")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("loginerror", "API Error: ${e.message}.")
        }
    }


    private fun checkIfUserExistsInDatabase(userName: String) {
        // Get a reference to your Firebase Realtime Database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersReference: DatabaseReference = database.reference.child("users")

        // Query the database to check if the username exists
        usersReference.orderByChild("displayName").equalTo(userName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Username already exists, fetch the user profile data
                        val userProfile: UserProfile? = dataSnapshot.children.firstOrNull()
                            ?.getValue(UserProfile::class.java)

                        if (userProfile != null) {
                            // Use the fetched data as needed
                            Log.d("FirebaseFetch", "Fetched user profile from the database: $userProfile")

                            // Fetch user profile data from Firebase after saving it
                            fetchUserProfileFromFirebase(userName)
                        } else {
                            Log.d("FirebaseFetch", "Error: User profile is null.")
                        }
                    } else {
                        // Username does not exist in the database, save a new userProfile
                        val userProfile = UserProfile(userName)
                        saveUserProfileToFirebase(userProfile)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FirebaseFetch", "Error: ${databaseError.message}")
                }
            })
    }

    private fun saveUserProfileToFirebase(userProfile: UserProfile) {
        try {
            // Get a reference to your Firebase Realtime Database
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val databaseReference: DatabaseReference = database.reference

            // Assume "users" is the root node for user profiles
            val usersReference: DatabaseReference = databaseReference.child("users")

            // Check if the username already exists
            usersReference.orderByChild("displayName").equalTo(userProfile.displayName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Username already exists, update the user profile
                            val existingUser = dataSnapshot.children.firstOrNull()

                            // Update userExp and userLevel
                            existingUser?.ref?.child("userExp")?.setValue(userProfile.userExp)

                            // Check if userExp is greater than or equal to 100
                            if (userProfile.userExp >= 100) {
                                // Calculate the number of level increases based on userExp
                                val levelIncrease = userProfile.userExp / 100

                                // Increase the userLevel by the calculated number
                                userProfile.userLevel += levelIncrease
                                existingUser?.ref?.child("userLevel")?.setValue(userProfile.userLevel)

                                // Reset userExp to the remainder after reaching 100
                                existingUser?.ref?.child("userExp")?.setValue(userProfile.userExp % 100)
                            }

                            Log.d("FirebaseSave", "User profile updated successfully")
                        } else {
                            // Generate a unique key for the user
                            val userId: String = usersReference.push().key ?: ""

                            // Save the UserProfile under the generated user ID
                            usersReference.child(userId).setValue(userProfile)

                            Log.d("FirebaseSave", "User profile saved successfully")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("FirebaseSave", "Error: ${databaseError.message}")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("FirebaseSave", "Error saving user profile: ${e.message}")
        }
    }
    private fun fetchUserProfileFromFirebase(userName: String) {
        // Fetch user profile data from Firebase after saving it
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersReference: DatabaseReference = database.reference.child("users")

        // Query the database to check if the username exists
        usersReference.orderByChild("displayName").equalTo(userName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Username already exists, fetch the user profile data
                        val userProfile: UserProfile? = dataSnapshot.children.firstOrNull()
                            ?.getValue(UserProfile::class.java)

                        if (userProfile != null) {
                            // Use the fetched data as needed
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

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
        Log.d("loginerror", "${exception.message}.")
        toast("Auth failed: ${exception.message}")
    }
}

