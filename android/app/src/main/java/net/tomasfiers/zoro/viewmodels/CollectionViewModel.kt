package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    repository: Repository
) : ViewModel() {

    val syncStatus = repository.syncStatus
    // Note: Transformations are executed on main thread, i.e. don't do heavy work here.
    val collections =
        Transformations.map(repository.collections) { collections ->
            collections
                .filter { it.parentId == collectionId }
                .sortedBy { it.name }
        }
}
