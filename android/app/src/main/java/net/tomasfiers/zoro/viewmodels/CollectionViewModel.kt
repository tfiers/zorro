package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.Repository
import java.text.Collator

class CollectionViewModelFactory(
    private val collectionId: String?,
    private val repository: Repository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CollectionViewModel(collectionId, repository) as T
}

// Makes sure "_PhD" comes before "Academia". `getInstance` depends on current default locale.
private fun compareStrings(x: String, y: String) =
    Collator.getInstance().compare(x, y)

class CollectionViewModel(
    private val collectionId: String?,
    private val repository: Repository
) : ViewModel() {

    val collectionName = MutableLiveData<String>()
    val isSyncing = repository.isSyncing
    // Note: Transformations are executed on main thread, so don't do heavy work here.
    val sortedCollections = Transformations.map(
        repository.getChildrenCollections(parentCollectionId = collectionId)
    ) {
        it
            .filter { collection -> collection.name != null }
            .sortedWith(Comparator { collection1, collection2 ->
                compareStrings(collection1.name!!, collection2.name!!)
            })
    }

    init {
        syncCollections()
        setCollectionName()
    }

    fun syncCollections() =
        viewModelScope.launch { repository.syncCollections() }

    private fun setCollectionName() =
        viewModelScope.launch {
            collectionName.value = when (collectionId) {
                null -> "My Library"
                else -> repository.getCollection(collectionId).name
            }
        }
}
