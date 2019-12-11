package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.tomasfiers.zoro.ZoroApplication
import net.tomasfiers.zoro.zotero_api.ZoteroAPIClient
import org.threeten.bp.Instant
import timber.log.Timber
import java.util.Timer
import java.util.concurrent.atomic.AtomicInteger

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
    // "Private" properties (only to be accessed by extension functions).
    var lastSyncTime: Instant? = null
    var lastSyncTextUpdateTimer = Timer()
    val numObjectsToDownload = MutableLiveData<Int?>(null)
    val numDownloadedObjects = MutableLiveData<Int?>(null)

    // For use by ViewModels
    val isSyncing = MutableLiveData<Boolean>(false)
    val syncStatus = MutableLiveData<String?>(null)
    val syncError = MutableLiveData<String?>(null)
    val lastSyncText = MutableLiveData<String>()
    val showProgressBar = MutableLiveData<Boolean>(false)
    val downloadProgress = Transformations.map(numDownloadedObjects) {
        (it ?: 0).toFloat() / (numObjectsToDownload.value ?: 1)
    }
}
