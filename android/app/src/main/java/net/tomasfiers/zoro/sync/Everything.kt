package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.BuildConfig
import net.tomasfiers.zoro.data.DataRepo
import org.threeten.bp.Instant.now

class RemoteLibraryUpdatedSignal : Exception()

suspend fun DataRepo.syncLibrary() {
    isSyncing.value = true
    syncError.value = null
    // There may be updates to the remote library *while* we are synching. We handle these by
    // checking the remote library version after each request, and restarting the synching procedure
    // if necessary.
    retrySyncLoop@ while (true) {
        try {
            syncSchema()
            val remoteLibraryVersion = syncCollections()
            // Update local libraray version only after all requests and database inserts have
            // completed.
            keyValStore.localLibraryVersion = remoteLibraryVersion ?: 0
            lastSyncTime = now()
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
    isSyncing.value = false
}
