package net.tomasfiers.zoro.viewmodels

import android.view.View
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

class MainActivityViewModel(dataRepo: DataRepo) : ViewModel() {
    val syncStatus = dataRepo.syncStatus
    val progressBarVisibility = Transformations.map(dataRepo.showProgressBar) {
        when (it) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }
    val maxProgress = 100
    val downloadProgress = Transformations.map(dataRepo.downloadProgress) { progressFraction ->
        ((progressFraction ?: 0f) * maxProgress).roundToInt()
    }
}
