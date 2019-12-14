package net.tomasfiers.zorro.ui.edititem

import androidx.lifecycle.ViewModel
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.util.ViewModelArgs

data class ContainerViewModelArgs(val itemKey: String) :
    ViewModelArgs

class ContainerViewModel(dataRepo: DataRepo, args: ContainerViewModelArgs) : ViewModel() {

    val item = dataRepo.database.item.get(args.itemKey)

}
