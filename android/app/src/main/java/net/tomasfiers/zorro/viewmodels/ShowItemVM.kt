package net.tomasfiers.zorro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.tomasfiers.zorro.data.DataRepo

class ShowItemViewModelFactory(
    private val itemKey: String,
    private val dataRepo: DataRepo
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ShowItemViewModel(itemKey, dataRepo) as T
}

class ShowItemViewModel(itemKey: String, dataRepo: DataRepo) : ViewModel() {

    val item = dataRepo.database.item.get(itemKey)

}
