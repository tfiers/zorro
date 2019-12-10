package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import net.tomasfiers.zoro.ZoroApplication
import net.tomasfiers.zoro.data.storage.KeyValStore
import net.tomasfiers.zoro.data.storage.ZoroDatabase
import org.threeten.bp.Instant

/**
 * Global state for the app. A singleton (by usage, not enforced), initialised in [ZoroApplication].
 *
 * Extension functions for this class in the `sync/` package keep the local database and the data on
 * zotero.org in sync. Extension functions in `CRUD.kt` are thin wrappers to access and modify local
 * data.
 * These functions (and the LiveData in this class) are used by ViewModels.
 */
class DataRepo(
    val database: ZoroDatabase,
    val keyValStore: KeyValStore
) {
    val isSyncing = MutableLiveData<Boolean>(false)
    val syncError = MutableLiveData<String?>(null)
    var lastSyncTime: Instant? = null
}
