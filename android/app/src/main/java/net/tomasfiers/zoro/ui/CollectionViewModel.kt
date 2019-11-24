package net.tomasfiers.zoro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.Collection
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient
import timber.log.Timber

class CollectionViewModelFactory(private val collectionId: String?) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CollectionViewModel(collectionId) as T
}

class CollectionViewModel(private val collectionId: String?) : ViewModel() {
    val syncStatus = MutableLiveData<String?>()

    private val allCollections = MutableLiveData<List<Collection>>(listOf())
    // Note: Transformations are executed on main thread, i.e. don't do heavy work here.
    val displayedCollections =
        Transformations.map(allCollections) { collections ->
            collections
                .filter { it.parentId == collectionId }
                .sortedBy { it.name }
        }

    private var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

    init {
        Timber.i("CollectionId: $collectionId")
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
                    allCollections.value =
                        allCollections.value!! + jsonCollections.map { it.asDomainModel() }
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
