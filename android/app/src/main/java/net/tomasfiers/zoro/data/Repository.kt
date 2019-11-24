package net.tomasfiers.zoro.data

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient

class Repository(private val coroutineScope: LifecycleCoroutineScope) {
    val syncStatus = MutableLiveData<String?>()
    val collections = MutableLiveData<List<Collection>>(listOf())

    init {
        getAllCollections()
    }

    private fun getAllCollections() {
        coroutineScope.launch {
            syncStatus.value = "Loading collections.."
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
                syncStatus.value = " "
            } catch (e: Exception) {
                syncStatus.value = "Failure: ${e.message}"
            }
        }
    }
}
