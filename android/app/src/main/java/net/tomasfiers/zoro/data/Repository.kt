package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import net.tomasfiers.zoro.ZoroApplication
import net.tomasfiers.zoro.zotero_api.ZoteroAPIClient
import org.threeten.bp.Instant
import java.util.Timer

/**
 * Global state for the app. A singleton (by usage, not enforced), initialised in [ZoroApplication].
 *
 * Extension functions for this class in the `sync/` package keep the local database in sync with
 * the data on zotero.org. Extension functions in `CRUD.kt` are thin wrappers to access and modify
 * local data.
 * These extension functions (and the LiveData in this class) are used by ViewModels.
 */
class DataRepo(
    val database: ZoroDatabase,
    val zoteroAPIClient: ZoteroAPIClient,
    val application: ZoroApplication
) {
    val isSyncing = MutableLiveData<Boolean>(false)
    val syncStatus = MutableLiveData<String>()
    val syncError = MutableLiveData<String?>(null)
    val downloadProgress = MutableLiveData<Float>(0f)

    // "Private" properties (only to be accessed by extension functions).
    var lastSyncTime: Instant? = null
    var lastSyncTextUpdateTimer = Timer()
}
