package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient

suspend fun DataRepo.syncCollections() {
    val collectionVersionsResponse = zoteroAPIClient.getCollectionVersions(
        sinceLibraryVersion = keyValStore.localLibraryVersion
    )
    remoteLibraryVersion = collectionVersionsResponse.remoteLibraryVersion
    val collectionIds = collectionVersionsResponse.body()?.keys ?: emptyList<String>()
    collectionIds.chunked(MAX_ITEMS_PER_RESPONSE).forEach { idListChunk ->
        val jsonCollectionsResponse =
            zoteroAPIClient.getCollections(idListChunk.joinToString(","))
        if (jsonCollectionsResponse.remoteLibraryVersion != remoteLibraryVersion) {
            throw RemoteLibraryUpdatedSignal()
        }
        jsonCollectionsResponse.body()?.forEach {
            database.collectionDAO.insert(it.asDomainModel())
        }
    }
}
