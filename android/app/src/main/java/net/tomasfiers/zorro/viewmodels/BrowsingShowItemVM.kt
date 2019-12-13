package net.tomasfiers.zorro.viewmodels

import androidx.lifecycle.ViewModel
import net.tomasfiers.zorro.data.DataRepo

data class BrowsingItemDetailViewModelArgs(val itemKey: String) : ViewModelArgs

class BrowsingItemDetailViewModel(dataRepo: DataRepo, args: BrowsingItemDetailViewModelArgs) :
    ViewModel() {

    val item = dataRepo.database.item.get(args.itemKey)
}
