package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.clearLocalData

class DrawerMenuViewModelFactory(
    private val dataRepo: DataRepo
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        DrawerMenuViewModel(dataRepo) as T
}

class DrawerMenuViewModel(private val dataRepo: DataRepo) : ViewModel() {

    val syncStatus = dataRepo.syncStatus

    fun clearLocalData() = viewModelScope.launch {
        dataRepo.clearLocalData()
    }
}
