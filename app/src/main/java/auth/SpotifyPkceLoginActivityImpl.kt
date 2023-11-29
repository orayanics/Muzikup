package auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.example.muzikup.BuildConfig
import com.example.muzikup.LoginActivity
import utils.SpotifyPlaygroundApplication
import utils.safeLet
import utils.toast

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {
    override val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    override val scopes = SpotifyScope.values().toList()

    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as SpotifyPlaygroundApplication).model
        model.credentialStore.setSpotifyApi(api)
        val classBackTo = pkceClassBackTo ?: LoginActivity::class.java
        pkceClassBackTo = null
        toast("Authentication has completed. Launching ${classBackTo.simpleName}..")
        startActivity(Intent(this, classBackTo))
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
        toast("Auth failed: ${exception.message}")
    }
}

fun toast(context: Context?, message: String?, duration: Int = Toast.LENGTH_SHORT) {
    safeLet(context, message, duration) { safeContext, safeMessage, safeDuration ->
        (safeContext as? Activity)?.runOnUiThread {
            Toast.makeText(safeContext, safeMessage, safeDuration).show()
        }
    }
}