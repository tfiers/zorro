package net.tomasfiers.zoro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.TreeItem
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient

class CollectionViewModel : ViewModel() {
    val collections = MutableLiveData<List<TreeItem>>(listOf())
    val syncStatus = MutableLiveData<String?>()

    private var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

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
                    totalResults = response.headers()["Total-Results"]!!.toLong()
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
