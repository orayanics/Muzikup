package auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.example.muzikup.BuildConfig
import com.example.muzikup.SearchFeedActivity
import com.example.muzikup.SpotifyPlaygroundApplication
import utils.toast

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {
    override val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    override val scopes = SpotifyScope.values().toList()

    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as SpotifyPlaygroundApplication).model
        model.credentialStore.setSpotifyApi(api)
        val classBackTo = pkceClassBackTo ?: SearchFeedActivity::class.java
        resetStateForNewUser()
       // pkceClassBackTo = null
        toast("Authentication has completed. Launching ${classBackTo.simpleName}..")

        startActivity(Intent(this, classBackTo))
    }

    private fun resetStateForNewUser() {
        // Reset any state related to the previous user
        pkceClassBackTo = null

        // Clear existing tokens and set the credential store to a clean state
//        val model = (application as SpotifyPlaygroundApplication).model
//        model.credentialStore.clear()
    }

     suspend fun getId(api: SpotifyClientApi) {
        val user = api.getUserId()
        toast("Authentication has completed! $user")
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
        Log.d("loginerror", "${exception.message}.")
        toast("Auth failed: ${exception.message}")
    }
}
