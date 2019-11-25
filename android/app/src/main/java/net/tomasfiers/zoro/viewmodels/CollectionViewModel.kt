package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.repository
import java.text.Collator

class CollectionViewModelFactory(
    private val collectionId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CollectionViewModel(collectionId) as T
}

// Makes sure "_PhD" comes before "Academia". `getInstance` depends on current default locale.
private fun compareStrings(x: String, y: String) = Collator.getInstance().compare(x, y)

class CollectionViewModel(
    private val collectionId: String?
) : ViewModel() {

    val isSynching = repository.isSynching

    // Note: Transformations are executed on main thread, i.e. don't do heavy work here.
    val collections =
        Transformations.map(repository.collections) {
            it
                .filter { coll -> coll.parentId == collectionId }
                .filter { coll -> coll.name != null }
                .sortedWith(Comparator { coll1, coll2 ->
                    compareStrings(coll1.name!!, coll2.name!!)
                })
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
