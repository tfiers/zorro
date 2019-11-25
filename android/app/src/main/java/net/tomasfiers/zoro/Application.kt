package net.tomasfiers.zoro

import android.app.Application
import net.tomasfiers.zoro.data.Repository
import net.tomasfiers.zoro.data.ZoroDatabase
import timber.log.Timber

class ZoroApplication : Application() {

    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        val database = ZoroDatabase.getDatabase(applicationContext)
        repository = Repository(database)
        Timber.plant(Timber.DebugTree())
    }
}
