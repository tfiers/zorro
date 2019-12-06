package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient
import org.threeten.bp.Instant
import org.threeten.bp.Instant.now

class DataRepository(
    private val database: ZoroDatabase,
    private val keyValStore: KeyValStore
) {

    val isSyncing = MutableLiveData<Boolean>(false)
    val syncError = MutableLiveData<String?>(null)
    var lastSyncTime: Instant? = null

    fun getChildrenCollections(parentCollectionId: String?) =
        database.collectionDAO.getChildren(parentCollectionId)

    suspend fun getCollection(collectionId: String) = database.collectionDAO.get(collectionId)

    suspend fun clearLocalData() {
        keyValStore.localLibraryVersion = INITIAL_LOCAL_LIBRARY_VERSION
        database.collectionDAO.clearCollections()
    }

    suspend fun syncCollections() {
        isSyncing.value = true
        syncError.value = null
        try {
            val response = zoteroAPIClient.getCollectionVersions(
                sinceLibraryVersion = keyValStore.localLibraryVersion
            )
            val remoteLibraryVersion = response.headers()["Last-Modified-Version"]?.toInt() ?: 0
            val collectionIds = response.body()?.keys ?: emptyList<String>()
            collectionIds.chunked(MAX_ITEMS_PER_RESPONSE).forEach { idListChunk ->
                val jsonCollections = zoteroAPIClient.getCollections(idListChunk.joinToString(","))
                jsonCollections.forEach {
                    database.collectionDAO.insert(it.asDomainModel())
                }
            }
            // Note: we do not mark sync as succesful (and local library version as updated) until
            // all requests and database inserts have completed.
            keyValStore.localLibraryVersion = remoteLibraryVersion
            lastSyncTime = now()
        } catch (e: Exception) {
            syncError.value = e.message
        }
        isSyncing.value = false
    }
}
