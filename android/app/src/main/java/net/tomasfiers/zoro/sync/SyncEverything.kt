package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.BuildConfig
import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.INITIAL_LOCAL_LIBRARY_VERSION
import net.tomasfiers.zoro.data.Key
import net.tomasfiers.zoro.data.setValue
import org.threeten.bp.Instant.now

class RemoteLibraryUpdatedSignal : Exception()

suspend fun DataRepo.syncLibrary() {
    isSyncing.value = true
    syncError.value = null
    stopUpdatingLastSyncText()
    lastSyncText.value = "Syncing with zotero.org…"
    // There may be updates to the remote library *while* we are synching. We handle these by
    // checking the remote library version after each request, and restarting the synching procedure
    // if necessary.
    retrySyncLoop@ while (true) {
        try {
            syncSchema()
            val remoteLibVersionAtStartSync = syncCollections()
            syncItems(remoteLibVersionAtStartSync)
            // Update local library version only after all requests and database inserts have
            // completed.
            setValue(
                Key.LOCAL_LIBRARY_VERSION,
                remoteLibVersionAtStartSync ?: INITIAL_LOCAL_LIBRARY_VERSION
            )
            lastSyncTime = now()
            startUpdatingLastSyncText()
            break@retrySyncLoop
        } catch (e: RemoteLibraryUpdatedSignal) {
            continue@retrySyncLoop
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            } else {
                syncError.value = e.message
                break@retrySyncLoop
            }
        }
    }
    syncStatus.value = null
    isSyncing.value = false
}
