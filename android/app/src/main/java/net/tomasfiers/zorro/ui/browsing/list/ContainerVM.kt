package net.tomasfiers.zorro.ui.browsing.list

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.entities.ListElement
import net.tomasfiers.zorro.sync.syncLibrary
import net.tomasfiers.zorro.util.ViewModelArgs
import java.text.Collator


data class ContainerViewModelArgs(val collectionKey: String?) :
    ViewModelArgs

class ContainerViewModel(
    private val dataRepo: DataRepo,
    args: ContainerViewModelArgs
) : ViewModel() {

    val isSyncing = dataRepo.isSyncing
    private val collectionName = MutableLiveData<String>()
    // Note: Transformations are executed on main thread, so don't do heavy work here.
    private val sortedCollections = Transformations.map(
        dataRepo.database.collection.getChildren(parentKey = args.collectionKey)
    ) {
        it
            .sortedWith(Comparator { collection1, collection2 ->
                compareStrings(
                    collection1.name,
                    collection2.name
                )
            })
    }
    private val items = dataRepo.database.item.getChildren(args.collectionKey)
    val listElements = MediatorLiveData<List<ListElement>>()
    val concatListElements = {
        listElements.value =
            (sortedCollections.value ?: listOf()) + (items.value ?: listOf())
    }

    init {
        listElements.addSource(sortedCollections) { concatListElements() }
        listElements.addSource(items) { concatListElements() }
    }

    fun syncLibrary() =
        viewModelScope.launch { dataRepo.syncLibrary() }
}

// Makes sure "_PhD" comes before "Academia". `getInstance` depends on current default locale.
private fun compareStrings(x: String, y: String) =
    Collator.getInstance().compare(x, y)
