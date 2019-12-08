package net.tomasfiers.zoro

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.storage.KeyValStore
import net.tomasfiers.zoro.data.storage.ZoroDatabase
import timber.log.Timber

class ZoroApplication : Application() {

    lateinit var dataRepo: DataRepo

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.i("Development mode ")
        }
        val database = ZoroDatabase.getDatabase(applicationContext)
        val keyValStore = KeyValStore(this)
        dataRepo = DataRepo(database, keyValStore)
    }
}
