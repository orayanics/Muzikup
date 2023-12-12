package auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.adamratzman.spotify.models.SpotifyUserInformation
import com.example.muzikup.BuildConfig
import com.example.muzikup.FeedActivity
import com.example.muzikup.MainActivity
import com.example.muzikup.SearchFeedActivity
import com.example.muzikup.SpotifyPlaygroundApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.toast

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {
    override val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    //override val scopes = SpotifyScope.values().toList()
    override val scopes = (SpotifyScope.values() + SpotifyScope.USER_READ_EMAIL + SpotifyScope.USER_READ_PRIVATE).toList()


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
//            val userPrivate: SpotifyUserInformation = api.users.getClientProfile()
//            val userId = userPrivate.id
//            Log.d("login", "User's ID: $userId")

            //Fetch user's private information (including email and profile picture)
            val userPrivate: SpotifyUserInformation = api.users.getClientProfile()
            val userId = userPrivate.id
            val email = userPrivate.email
            val profilePictureUrl = userPrivate.images?.firstOrNull()?.url

            Log.d("login", "User's Email: $email")
            Log.d("login", "Profile Picture URL: $profilePictureUrl")

            toast("User's ID: $userId\nUser's Email: $email\nProfile Picture URL: $profilePictureUrl")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("loginerror", "API Error: ${e.message}.")
            toast("Error fetching user information: ${e.message}")
        }
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
        Log.d("loginerror", "${exception.message}.")
        toast("Auth failed: ${exception.message}")
    }
}

