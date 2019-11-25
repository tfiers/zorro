package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.Repository

class CollectionViewModelFactory(
    private val collectionId: String?,
    private val repository: Repository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CollectionViewModel(collectionId, repository) as T
}

class CollectionViewModel(
    private val collectionId: String?,
    private val repository: Repository
) : ViewModel() {

    val isSynching = repository.isSynching
    // Note: Transformations are executed on main thread, i.e. don't do heavy work here.
    val collections =
        Transformations.map(repository.collections) { collections ->
            collections
                .filter { it.parentId == collectionId }
                .sortedBy { it.name }
        }
    val collectionName = when (collectionId) {
        null -> "My Library"
        else -> repository.getCollection(collectionId)?.name
    }

    fun onPulledToRefresh() {
        viewModelScope.launch {
            repository.getAllCollections()
        }
    }
}
