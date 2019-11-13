package net.tomasfiers.zoro

import android.app.Application
import timber.log.Timber

class ZoroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}