package net.tomasfiers.zorro.ui.browsing.itemdetail

import androidx.lifecycle.ViewModel
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.util.ViewModelArgs

data class BrowsingItemDetailViewModelArgs(val itemKey: String) :
    ViewModelArgs

class BrowsingItemDetailViewModel(dataRepo: DataRepo, args: BrowsingItemDetailViewModelArgs) :
    ViewModel() {

    val item = dataRepo.database.item.get(args.itemKey)
}
