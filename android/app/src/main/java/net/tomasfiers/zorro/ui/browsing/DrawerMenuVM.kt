package net.tomasfiers.zorro.ui.browsing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.clearLocalData
import net.tomasfiers.zorro.data.getValue
import net.tomasfiers.zorro.data.setValue

class DrawerMenuViewModel(private val dataRepo: DataRepo) : ViewModel() {

    val lastSyncText = dataRepo.lastSyncText
    val developerMode = MutableLiveData<Boolean>()
    val developerModeToggled = MutableLiveData<Boolean>(false)

    init {
        viewModelScope.launch {
            developerMode.value = dataRepo.getValue(Key.DEVELOPER_MODE)
        }
    }

    fun toggleDeveloperMode() {
        developerMode.value = !developerMode.value!!
        viewModelScope.launch {
            dataRepo.setValue(Key.DEVELOPER_MODE, developerMode.value)
        }
        // We only want to display a message the first time developer mode is toggled on or off.
        if (developerModeToggled.value == false) {
            developerModeToggled.value = true
        }
    }

    fun clearLocalData() = viewModelScope.launch {
        dataRepo.clearLocalData()
    }
}
