package net.tomasfiers.zorro.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.zotero_api.ZoteroAPIClient
import org.threeten.bp.Instant
import java.util.Timer

/**
 * Global state for the app. A singleton (by usage, not enforced), initialised in [ZorroApplication].
 *
 * Extension functions for this class in the `sync/` package keep the local database in sync with
 * the data on zotero.org.
 * These extension functions (and the LiveData in this class) are used by ViewModels.
 */
class DataRepo(
    val database: ZorroDatabase,
    val zoteroAPIClient: ZoteroAPIClient,
    val application: ZorroApplication
) {
    // "Private" properties (only to be accessed by extension functions).
    var lastSyncTime: Instant? = null
    var lastSyncTextUpdateTimer = Timer()
    val numRequests = MutableLiveData<Int?>(null)
    val numCompletedRequests = MutableLiveData<Int?>(null)

    // For use by ViewModels
    val isSyncing = MutableLiveData<Boolean>(false)
    val syncStatus = MutableLiveData<String?>(null)
    val syncError = MutableLiveData<String?>(null)
    val lastSyncText = MutableLiveData<String>()
    val showProgressBar = MutableLiveData<Boolean>(false)
    val downloadProgress = Transformations.map(numCompletedRequests) {
        (it ?: 0).toFloat() / (numRequests.value ?: 1)
    }
    // In developer mode, we navigate straight to a subcollection at startup.
    var autoNavigatedToSubcollection = false
}

/**
 * Note that this `clearAllTables` is not a suspending function; It fires an asynchronous task
 * without callback. There is thus no way to know when the database is actually cleared (other than
 * manually checking if data is still present).
 */
suspend fun DataRepo.clearLocalData() =
    withContext(Dispatchers.IO) { database.clearAllTables() }
