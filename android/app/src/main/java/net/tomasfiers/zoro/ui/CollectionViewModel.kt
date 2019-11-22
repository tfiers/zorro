package net.tomasfiers.zoro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.ListItem
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient

class CollectionViewModel : ViewModel() {
    val collections = MutableLiveData<List<ListItem>>()
    val syncStatus = MutableLiveData<String?>()

    private var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

    init {
        coroutineScope.launch {
            syncStatus.value = "Loading collections.."
            try {
                val jsonCollections = zoteroAPIClient.getSomeCollections().await()
                collections.value = jsonCollections.map { it.asDomainModel() }
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
