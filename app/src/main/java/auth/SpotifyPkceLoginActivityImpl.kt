package auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.example.muzikup.BuildConfig
import com.example.muzikup.SearchFeedActivity
import com.example.muzikup.SpotifyPlaygroundApplication
import api.UserProfile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import utils.safeLet
import utils.toast

internal var pkceClassBackTo: Class<out Activity>? = null

interface SpotifyApiService {
    @GET("/v1/users/{userId}")
    fun getUserProfile(@Path("userId") userId: String): Call<UserProfile>
}

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val spotifyApiService: SpotifyApiService = retrofit.create(SpotifyApiService::class.java)

    override val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    override val scopes = SpotifyScope.values().toList()

    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as SpotifyPlaygroundApplication).model
        model.credentialStore.setSpotifyApi(api)
        val classBackTo = pkceClassBackTo ?: SearchFeedActivity::class.java
        pkceClassBackTo = null
        toast("Authentication has completed. Launching ${classBackTo.simpleName}..")

        // Fetch user information
        GlobalScope.launch {
            // Fetch user information
            fetchUserInfo(api)

            startActivity(Intent(this@SpotifyPkceLoginActivityImpl, classBackTo))
        }
    }
    suspend fun getId(api: SpotifyClientApi) {
        val user = api.getUserId()
        toast("Authentication has completed! $user")
    }

    suspend fun fetchUserInfo(api: SpotifyClientApi) {
        val userId = api.getUserId()

        // Make the API call using Retrofit
        val call = spotifyApiService.getUserProfile(userId)
        try {
            val response = call.execute()

            Log.d("UserInfo", "Request URL: ${call.request().url}")
            Log.d("UserInfo", "Request Headers: ${call.request().headers}")

            if (response.isSuccessful) {
                val user = response.body()
                val userEmail = user?.email
                val userPhotoUrl = user?.images?.firstOrNull()?.url

                Log.d("UserInfo", "Email: $userEmail, Photo: $userPhotoUrl")
            } else {
                Log.d("UserInfo", "Failed to get user profile: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d("UserInfo", "Exception while getting user profile: ${e.message}")
        }
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
        Log.d("loginerror", "${exception.message}.")
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