package net.tomasfiers.zoro.viewmodels

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.tomasfiers.zoro.data.DataRepo
import kotlin.math.roundToInt

class MainActivityViewModelFactory(
    private val dataRepo: DataRepo
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MainActivityViewModel(dataRepo) as T
}

class MainActivityViewModel(private val dataRepo: DataRepo) : ViewModel() {
    val syncStatus = dataRepo.syncStatus
    val maxProgress = 100
    val downloadProgress = Transformations.map(dataRepo.downloadProgress) {
        (it * maxProgress).roundToInt()
    }
}
