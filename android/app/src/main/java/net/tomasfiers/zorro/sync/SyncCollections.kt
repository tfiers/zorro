package net.tomasfiers.zorro.sync

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.getValue
import net.tomasfiers.zorro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zorro.zotero_api.remoteLibraryVersion
import java.util.concurrent.atomic.AtomicInteger

suspend fun DataRepo.syncCollections(): Int? {
    syncStatus.value = "Checking for updated collections…"
    val collectionVersionsResponse = zoteroAPIClient.getCollectionVersions(
        sinceLibraryVersion = getValue(Key.LOCAL_LIBRARY_VERSION)
    )
    val remoteLibVersionAtStartSync = collectionVersionsResponse.remoteLibraryVersion
    val collectionIds = collectionVersionsResponse.body()?.keys ?: emptyList<String>()
    if (collectionIds.isNotEmpty()) {
        syncStatus.value = "Downloading ${collectionIds.size} collections…"
        numCompletedRequests.value = 0
        val chunkedCollectionIds = collectionIds.chunked(MAX_ITEMS_PER_RESPONSE)
        numRequests.value = chunkedCollectionIds.size
        if (numRequests.value ?: 0 > 5) {
            showProgressBar.value = true
        }
        val numCompletedRequestsAtomic = AtomicInteger(0)
        coroutineScope {
            chunkedCollectionIds.forEach { someCollectionIds ->
                launch {
                    val jsonCollectionsResponse =
                        zoteroAPIClient.getCollections(someCollectionIds.joinToString(","))
                    if (jsonCollectionsResponse.remoteLibraryVersion != remoteLibVersionAtStartSync) {
                        throw RemoteLibraryUpdatedSignal()
                    }
                    database.collection.insert(jsonCollectionsResponse.body()!!.map { it.asDomainModel() })
                    numCompletedRequests.value = numCompletedRequestsAtomic.incrementAndGet()
                }
            }
        }
        showProgressBar.value = false
    }
    return remoteLibVersionAtStartSync
}
