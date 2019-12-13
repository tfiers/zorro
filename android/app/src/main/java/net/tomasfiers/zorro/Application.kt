package net.tomasfiers.zorro

import android.app.Application
import androidx.fragment.app.Fragment
import com.jakewharton.threetenabp.AndroidThreeTen
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.ZorroDatabase
import net.tomasfiers.zorro.zotero_api.createZoteroAPIClient
import timber.log.Timber

class ZorroApplication : Application() {

    lateinit var dataRepo: DataRepo

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.i("Development mode ")
        }
        val database = ZorroDatabase.getDatabase(applicationContext)
        val zoteroAPIClient = createZoteroAPIClient()
        dataRepo = DataRepo(database, zoteroAPIClient, this)
    }
}

// Inject dataRepo in each Fragment
val Fragment.dataRepo: DataRepo
    get() = (requireActivity().application as ZorroApplication).dataRepo

