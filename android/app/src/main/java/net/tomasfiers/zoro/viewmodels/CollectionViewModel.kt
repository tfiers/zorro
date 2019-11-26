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
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import java.text.Collator
import java.util.Timer
import kotlin.Comparator
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
    val isSyncing = application.repository.isSyncing
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
    val lastSyncText = MutableLiveData<String>()
    private val isSyncingObserver = Observer<Boolean> { startUpdatingLastSyncText() }
    private var lastSyncTextUpdateTimer = Timer()

    init {
        syncCollections()
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

    private fun startUpdatingLastSyncText() {
        if (isSyncing.value == true) {
            lastSyncTextUpdateTimer.cancel()
        } else {
            lastSyncTextUpdateTimer = Timer()
            lastSyncTextUpdateTimer.schedule(
                timerTask { viewModelScope.launch { updateLastSyncText() } },
                0,
                MINUTE_IN_MILLIS
            )
        }
    }

    private fun updateLastSyncText() {
        val lastSyncTime = application.repository.lastSyncTime
        lastSyncText.value = when (lastSyncTime) {
            null -> ""
            else -> {
                if (Duration.between(lastSyncTime, Instant.now()).toMinutes() < 1) {
                    "just now"
                } else {
                    getRelativeDateTimeString(
                        application.applicationContext,
                        lastSyncTime.toEpochMilli(),
                        MINUTE_IN_MILLIS,
                        WEEK_IN_MILLIS,
                        0
                    ).toString()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isSyncing.removeObserver(isSyncingObserver)
        lastSyncTextUpdateTimer.cancel()
    }
}
