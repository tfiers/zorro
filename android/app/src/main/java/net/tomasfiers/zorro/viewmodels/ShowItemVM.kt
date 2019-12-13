package net.tomasfiers.zorro.viewmodels

import androidx.lifecycle.ViewModel
import net.tomasfiers.zorro.data.DataRepo

data class ShowItemViewModelArgs(val itemKey: String) : ViewModelArgs

class ShowItemViewModel(dataRepo: DataRepo, args: ShowItemViewModelArgs) : ViewModel() {

    val item = dataRepo.database.item.get(args.itemKey)

}
