package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.BuildConfig
import net.tomasfiers.zoro.data.DataRepo
import org.threeten.bp.Instant.now

class RemoteLibraryUpdatedSignal : Exception()

suspend fun DataRepo.syncLibrary() {
    isSyncing.value = true
    syncError.value = null
    var remoteLibraryVersion: Int? = null
    // We handle updates to the remote library *while* we are synching, by checking the remote
    // library version after each request, and restarting the synching procedure if necessary.
    retrySyncLoop@ while (true) {
        try {
            syncSchema()
            remoteLibraryVersion = syncCollections()
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
    // Note: we do not mark sync as succesful (and local library version as updated) until all
    // requests and database inserts have completed.
    keyValStore.localLibraryVersion = remoteLibraryVersion ?: 0
    lastSyncTime = now()
    isSyncing.value = false
}
