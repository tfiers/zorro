package net.tomasfiers.zoro.viewmodels

import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.WEEK_IN_MILLIS
import android.text.format.DateUtils.getRelativeDateTimeString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.ZoroApplication
import java.util.Timer
import kotlin.concurrent.timerTask

class DrawerMenuViewModelFactory(
    private val application: ZoroApplication
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        DrawerMenuViewModel(application) as T
}

// Subclass AndroidViewModel so we can access `application.applicationContext`
class DrawerMenuViewModel(private val application: ZoroApplication) :
    AndroidViewModel(application) {

    val syncStatus = MutableLiveData<String>()
    private val isSyncing = application.dataRepo.isSyncing
    private val isSyncingObserver = Observer<Boolean> { updateSyncStatus() }
    private var syncStatuspdateTimer = Timer()

    init {
        isSyncing.observeForever(isSyncingObserver)
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
        val lastSyncTime = application.dataRepo.lastSyncTime
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

    fun clearLocalData() = viewModelScope.launch {
        application.dataRepo.clearLocalData()
    }
}
