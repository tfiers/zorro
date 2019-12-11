package net.tomasfiers.zoro.sync

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.Key
import net.tomasfiers.zoro.data.getValue
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion
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
        showProgressBar.value = true
        val chunkedCollectionIds = collectionIds.chunked(MAX_ITEMS_PER_RESPONSE)
        numRequests.value = chunkedCollectionIds.size
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
