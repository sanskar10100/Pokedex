package dev.sanskar.pokedex

import android.app.Application
import android.content.res.Configuration
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority

@HiltAndroidApp
class PokedexApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
    }
}