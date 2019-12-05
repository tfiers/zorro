package net.tomasfiers.zoro.viewmodels

import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.WEEK_IN_MILLIS
import android.text.format.DateUtils.getRelativeDateTimeString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.ZoroApplication
import java.text.Collator
import java.util.Timer
import kotlin.concurrent.timerTask

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
) : AndroidViewModel(application) {

    val collectionName = MutableLiveData<String>()
    // Note: Transformations are executed on main thread, so don't do heavy work here.
    val sortedCollections = Transformations.map(
        application.repository.getChildrenCollections(parentCollectionId = collectionId)
    ) {
        it
            .filter { collection -> collection.name != null }
            .sortedWith(Comparator { collection1, collection2 ->
                compareStrings(collection1.name!!, collection2.name!!)
            })
    }
    val syncStatus = MutableLiveData<String>()
    val isSyncing = application.repository.isSyncing
    private val isSyncingObserver = Observer<Boolean> { updateSyncStatus() }
    private var syncStatuspdateTimer = Timer()

    init {
        setCollectionName()
        isSyncing.observeForever(isSyncingObserver)
    }

    fun syncCollections() =
        viewModelScope.launch { application.repository.syncCollections() }

    private fun setCollectionName() =
        viewModelScope.launch {
            collectionName.value = when (collectionId) {
                null -> "My Library"
                else -> application.repository.getCollection(collectionId).name
            }
        }

    private fun updateSyncStatus() {
        if (isSyncing.value == true) {
            syncStatuspdateTimer.cancel()
            syncStatus.value = "Syncing with Zotero API.."
        } else {
            syncStatuspdateTimer = Timer()
            syncStatuspdateTimer.schedule(
                timerTask { viewModelScope.launch { setLastSyncTime() } },
                0,
                MINUTE_IN_MILLIS
            )
        }
    }

    private fun setLastSyncTime() {
        val lastSyncTime = application.repository.lastSyncTime
        syncStatus.value = when (lastSyncTime) {
            null -> ""
            else -> "Last sync: " + getRelativeDateTimeString(
                application.applicationContext,
                lastSyncTime.toEpochMilli(),
                MINUTE_IN_MILLIS,
                WEEK_IN_MILLIS,
                0
            ).toString()
                .replace(Regex("^0 minutes ago"), "just now")
        }
    }

    override fun onCleared() {
        super.onCleared()
        isSyncing.removeObserver(isSyncingObserver)
        syncStatuspdateTimer.cancel()
    }
}
