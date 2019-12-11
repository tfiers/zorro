package net.tomasfiers.zorro.sync

import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.getValue
import net.tomasfiers.zorro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zorro.zotero_api.remoteLibraryVersion

suspend fun DataRepo.syncCollections(): Int? {
    syncStatus.value = "Checking for updated collections…"
    val collectionVersionsResponse = zoteroAPIClient.getCollectionVersions(
        sinceLibraryVersion = getValue(Key.LOCAL_LIBRARY_VERSION)
    )
    val remoteLibVersionAtStartSync = collectionVersionsResponse.remoteLibraryVersion
    val collectionKeys = collectionVersionsResponse.body()?.keys ?: emptyList<String>()
    if (collectionKeys.isNotEmpty()) {
        syncStatus.value = "Downloading ${collectionKeys.size} collections…"
        val chunkedCollectionKeys = collectionKeys.chunked(MAX_ITEMS_PER_RESPONSE)
        val argsList = chunkedCollectionKeys.map { someCollectionKeys ->
            DownloadSomeCollectionsArgs(someCollectionKeys, remoteLibVersionAtStartSync)
        }
        makeConcurrentRequests(DataRepo::downloadSomeCollections, argsList)
    }
    return remoteLibVersionAtStartSync
}

class DownloadSomeCollectionsArgs(
    val collectionKeys: List<String>,
    val remoteLibVersionAtStartSync: Int?
) : FunctionArgs()

suspend fun DataRepo.downloadSomeCollections(args: DownloadSomeCollectionsArgs) {
    val jsonCollectionsResponse =
        zoteroAPIClient.getCollections(args.collectionKeys.joinToString(","))
    if (jsonCollectionsResponse.remoteLibraryVersion != args.remoteLibVersionAtStartSync) {
        throw RemoteLibraryUpdatedSignal()
    }
    database.collection.insert(jsonCollectionsResponse.body()!!.map { it.asDomainModel() })
}
