package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.getChildrenCollections
import net.tomasfiers.zoro.data.getCollection
import java.text.Collator

class CollectionViewModelFactory(
    private val collectionKey: String?,
    private val dataRepo: DataRepo
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CollectionViewModel(collectionKey, dataRepo) as T
}

// Makes sure "_PhD" comes before "Academia". `getInstance` depends on current default locale.
private fun compareStrings(x: String, y: String) =
    Collator.getInstance().compare(x, y)

class CollectionViewModel(
    private val collectionKey: String?,
    private val dataRepo: DataRepo
) : ViewModel() {

    val collectionName = MutableLiveData<String>()
    // Note: Transformations are executed on main thread, so don't do heavy work here.
    val sortedCollections = Transformations.map(
        getChildrenCollections(parentCollectionKey = collectionKey, dataRepo = dataRepo)
    ) {
        it
            .sortedWith(Comparator { collection1, collection2 ->
                compareStrings(collection1.name, collection2.name)
            })
    }
    val isSyncing = dataRepo.isSyncing

    init {
        setCollectionName()
    }

    fun syncLibrary() =
        viewModelScope.launch { net.tomasfiers.zoro.sync.syncLibrary(dataRepo) }

    private fun setCollectionName() =
        viewModelScope.launch {
            collectionName.value = when (collectionKey) {
                null -> "My Library"
                else -> getCollection(collectionKey, dataRepo).name
            }
        }
}
