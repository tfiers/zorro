package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import net.tomasfiers.zoro.data.storage.KeyValStore
import net.tomasfiers.zoro.data.storage.ZoroDatabase
import org.threeten.bp.Instant

// Global state. A singleton (by convention, not enforced), initialised in ZoroApplication().
class DataRepo(
    val database: ZoroDatabase,
    val keyValStore: KeyValStore
) {
    val isSyncing = MutableLiveData<Boolean>(false)
    val syncError = MutableLiveData<String?>(null)
    var lastSyncTime: Instant? = null
}
