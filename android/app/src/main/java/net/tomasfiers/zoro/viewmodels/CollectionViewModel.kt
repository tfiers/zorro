package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.ZoroApplication
import java.text.Collator

class CollectionViewModelFactory(
    private val collectionId: String?,
    private val application: ZoroApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CollectionViewModel(collectionId, application) as T
}

// Makes sure "_PhD" comes before "Academia". `getInstance` depends on current default locale.
private fun compareStrings(x: String, y: String) =
    Collator.getInstance().compare(x, y)

class CollectionViewModel(
    private val collectionId: String?,
    private val application: ZoroApplication
) : ViewModel() {

    val collectionName = MutableLiveData<String>()
    // Note: Transformations are executed on main thread, so don't do heavy work here.
    val sortedCollections = Transformations.map(
        application.dataRepo.getChildrenCollections(parentCollectionId = collectionId)
    ) {
        it
            .filter { collection -> collection.name != null }
            .sortedWith(Comparator { collection1, collection2 ->
                compareStrings(collection1.name!!, collection2.name!!)
            })
    }
    val isSyncing = application.dataRepo.isSyncing

    init {
        setCollectionName()
    }

    fun syncCollections() =
        viewModelScope.launch { application.dataRepo.syncCollections() }

    private fun setCollectionName() =
        viewModelScope.launch {
            collectionName.value = when (collectionId) {
                null -> "My Library"
                else -> application.dataRepo.getCollection(collectionId).name
            }
        }
}
