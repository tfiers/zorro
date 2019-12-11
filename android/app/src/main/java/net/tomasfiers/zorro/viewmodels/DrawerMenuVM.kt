package net.tomasfiers.zorro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.clearLocalData

class DrawerMenuViewModelFactory(
    private val dataRepo: DataRepo
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        DrawerMenuViewModel(dataRepo) as T
}

class DrawerMenuViewModel(private val dataRepo: DataRepo) : ViewModel() {

    val lastSyncText = dataRepo.lastSyncText

    fun clearLocalData() = viewModelScope.launch {
        dataRepo.clearLocalData()
    }
}