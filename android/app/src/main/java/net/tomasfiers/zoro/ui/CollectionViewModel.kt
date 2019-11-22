package net.tomasfiers.zoro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.zotero_api.ZoteroAPI

class CollectionViewModel : ViewModel() {
    val collections = MutableLiveData<String>("Loading collections..")
    private var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

    init {
        coroutineScope.launch {
            collections.value = try {
                ZoteroAPI.client.getSomeCollections().await().toString()
            } catch (e: Exception) {
                "Failure: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
