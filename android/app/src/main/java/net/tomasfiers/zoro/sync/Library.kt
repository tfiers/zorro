package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.BuildConfig
import net.tomasfiers.zoro.data.DataRepo
import org.threeten.bp.Instant.now

class RemoteLibraryUpdatedSignal : Exception()

suspend fun syncLibrary(dataRepo: DataRepo) {
    dataRepo.isSyncing.value = true
    dataRepo.syncError.value = null
    // We handle updates to the remote library *while* we are synching, by checking the remote
    // library version after each request, and restarting the synching procedure if necessary.
    retrySyncLoop@ while (true) {
        try {
            syncSchema(dataRepo)
            syncCollections(dataRepo)
            break@retrySyncLoop
        } catch (e: RemoteLibraryUpdatedSignal) {
            continue@retrySyncLoop
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            } else {
                dataRepo.syncError.value = e.message
                break@retrySyncLoop
            }
        }
    }
    // Note: we do not mark sync as succesful (and local library version as updated) until all
    // requests and database inserts have completed.
    dataRepo.keyValStore.localLibraryVersion = dataRepo.remoteLibraryVersion ?: 0
    dataRepo.lastSyncTime = now()
    dataRepo.isSyncing.value = false
}
