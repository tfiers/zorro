package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient

class Repository() {

    val isSynching = MutableLiveData<Boolean>(false)
    val synchingError = MutableLiveData<String?>()
    val collections = MutableLiveData<List<Collection>>()

    fun getCollection(collectionId: String?) = collections.value?.single { it.id == collectionId }

    suspend fun getAllCollections() {
        isSynching.value = true
        synchingError.value = null
        collections.value = listOf()
        try {
            var startIndex = 0
            var totalResults: Long
            do {
                val response = zoteroAPIClient.getSomeCollections(startIndex)
                totalResults = response.headers()["Total-Results"]?.toLong() ?: 0
                val jsonCollections = response.body()!!
                // We need to use assignment to update collections (and not append to a
                // MutableList), because only assignment (setValue) notifies observers.
                collections.value =
                    collections.value!! + jsonCollections.map { it.asDomainModel() }
                startIndex += MAX_ITEMS_PER_RESPONSE
            } while (startIndex < totalResults)
        } catch (e: Exception) {
            synchingError.value = "Synching error: ${e.message}"
        }
        isSynching.value = false
    }
}
