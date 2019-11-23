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
    val collections = MutableLiveData<List<ListItem>>(listOf())
    val syncStatus = MutableLiveData<String?>()

    private var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

    init {
        coroutineScope.launch {
            syncStatus.value = "Loading collections.."
            try {
                val numPerRequest = 3
                for (startIndex in 0 until 15 step numPerRequest) {
                    val jsonCollections = zoteroAPIClient.getSomeCollections(numPerRequest, startIndex)
                    // We need to use assignment (and not append to a MutableList), because only
                    // assignment (setValue) notifies observers.
                    collections.value =
                        collections.value!! + jsonCollections.map { it.asDomainModel() }
                }
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
