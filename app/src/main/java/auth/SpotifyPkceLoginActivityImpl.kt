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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        toast("Authentication has completed. Launching ${classBackTo.simpleName}..")
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


            Log.d("login", "User's ID: $userName, Prof: $profilePictureUrl")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("loginerror", "API Error: ${e.message}.")
        }
    }
    private suspend fun getUserDisplayName(api: SpotifyClientApi): String {
        return try {
            val userPrivate: SpotifyUserInformation = api.users.getClientProfile()
            userPrivate.displayName ?: "DefaultUsername"
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("loginerror", "API Error: ${e.message}.")
            "DefaultUsername"
        }
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
        Log.d("loginerror", "${exception.message}.")
        toast("Auth failed: ${exception.message}")
    }
}

