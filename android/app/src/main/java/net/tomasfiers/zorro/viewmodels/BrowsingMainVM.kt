package net.tomasfiers.zorro.viewmodels

import android.view.View
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.tomasfiers.zorro.data.DataRepo
import kotlin.math.roundToInt

class BrowsingMainViewModelFactory(
    private val dataRepo: DataRepo
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        BrowsingMainViewModel(dataRepo) as T
}

class BrowsingMainViewModel(dataRepo: DataRepo) : ViewModel() {
    val syncStatus = dataRepo.syncStatus
    val progressBarVisibility = Transformations.map(dataRepo.showProgressBar) { showProgressBar ->
        if (showProgressBar) View.VISIBLE else View.GONE
    }
    val maxProgress = 100
    val downloadProgress = Transformations.map(dataRepo.downloadProgress) { progressFraction ->
        ((progressFraction ?: 0f) * maxProgress).roundToInt()
    }
}
