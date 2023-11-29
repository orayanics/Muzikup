package utils
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import data.Model


class SpotifyPlaygroundApplication : Application() {
    lateinit var model: Model

    override fun onCreate() {
        super.onCreate()
        model = Model
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    companion object {
        lateinit var context: Context
    }
}