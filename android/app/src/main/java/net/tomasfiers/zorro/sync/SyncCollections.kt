package net.tomasfiers.zorro.sync

import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.getPersistentValue
import net.tomasfiers.zorro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zorro.zotero_api.remoteLibraryVersion

suspend fun DataRepo.syncCollections(): Int? {
    val collectionVersionsResponse = zoteroAPIClient.getCollectionVersions(
        sinceLibraryVersion = getPersistentValue(Key.LOCAL_LIBRARY_VERSION)
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

data class DownloadSomeCollectionsArgs(
    val collectionKeys: List<String>,
    val remoteLibVersionAtStartSync: Int?
)

suspend fun DataRepo.downloadSomeCollections(args: DownloadSomeCollectionsArgs) {
    val jsonCollectionsResponse =
        zoteroAPIClient.getCollections(args.collectionKeys.joinToString(","))
    if (jsonCollectionsResponse.remoteLibraryVersion != args.remoteLibVersionAtStartSync) {
        throw RemoteLibraryUpdatedSignal()
    }
    database.collection.insert(jsonCollectionsResponse.body()!!.map { it.asDomainModel() })
}
