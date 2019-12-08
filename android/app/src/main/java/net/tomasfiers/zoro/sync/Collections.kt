package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient

suspend fun syncCollections(dataRepo: DataRepo) {
    val collectionVersionsResponse = zoteroAPIClient.getCollectionVersions(
        sinceLibraryVersion = dataRepo.keyValStore.localLibraryVersion
    )
    dataRepo.remoteLibraryVersion = collectionVersionsResponse.remoteLibraryVersion
    val collectionIds = collectionVersionsResponse.body()?.keys ?: emptyList<String>()
    collectionIds.chunked(MAX_ITEMS_PER_RESPONSE).forEach { idListChunk ->
        val jsonCollectionsResponse =
            zoteroAPIClient.getCollections(idListChunk.joinToString(","))
        if (jsonCollectionsResponse.remoteLibraryVersion != dataRepo.remoteLibraryVersion) {
            throw RemoteLibraryUpdatedSignal()
        }
        jsonCollectionsResponse.body()?.forEach {
            dataRepo.database.collectionDAO.insert(it.asDomainModel())
        }
    }
}
