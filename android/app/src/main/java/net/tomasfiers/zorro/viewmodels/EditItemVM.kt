package net.tomasfiers.zorro.viewmodels

import androidx.lifecycle.ViewModel
import net.tomasfiers.zorro.data.DataRepo

data class EditItemViewModelArgs(val itemKey: String) : ViewModelArgs

class EditItemViewModel(dataRepo: DataRepo, args: EditItemViewModelArgs) : ViewModel() {

    val item = dataRepo.database.item.get(args.itemKey)

}
