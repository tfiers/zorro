package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient
import org.threeten.bp.Instant


class Repository(private val database: ZoroDatabase) {

    val isSyncing = MutableLiveData<Boolean>(false)
    val syncError = MutableLiveData<String?>(null)
    var lastSyncTime: Instant? = null

    fun getChildrenCollections(parentCollectionId: String?) =
        database.collectionDAO.getChildren(parentCollectionId)

    suspend fun getCollection(collectionId: String) =
        database.collectionDAO.get(collectionId)

    suspend fun syncCollections() {
        isSyncing.value = true
        syncError.value = null
        try {
            var startIndex = 0
            var totalResults: Long
            do {
                val response = zoteroAPIClient.getSomeCollections(startIndex)
                totalResults = response.headers()["Total-Results"]?.toLong() ?: 0
                response.body()!!.map {
                    database.collectionDAO.insert(it.asDomainModel())
                }
                startIndex += MAX_ITEMS_PER_RESPONSE
            } while (startIndex < totalResults)
            lastSyncTime = Instant.now()
        } catch (e: Exception) {
            syncError.value = "Synching error (${e.message})"
        }
        isSyncing.value = false
    }
}
