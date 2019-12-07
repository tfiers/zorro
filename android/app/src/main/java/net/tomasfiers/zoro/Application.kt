package net.tomasfiers.zoro

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import net.tomasfiers.zoro.data.KeyValStore
import net.tomasfiers.zoro.data.Repository
import net.tomasfiers.zoro.data.ZoroDatabase
import timber.log.Timber

class ZoroApplication : Application() {

    lateinit var dataRepo: Repository

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())
        val database = ZoroDatabase.getDatabase(applicationContext)
        val keyValStore = KeyValStore(this)
        dataRepo = Repository(database, keyValStore)
    }
}
