package net.tomasfiers.zorro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.tomasfiers.zorro.data.DataRepo


interface ViewModelArgs

class ZorroViewModelFactory<A : ViewModelArgs>(
    private val dataRepo: DataRepo,
    private val otherArgs: A? = null
) : ViewModelProvider.Factory {

    private val viewModelClasses = listOf(
        BrowsingContainerViewModel::class.java,
        BrowsingDrawerMenuViewModel::class.java,
        BrowsingListViewModel::class.java,
        ShowItemViewModel::class.java
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModelClass = viewModelClasses.first { modelClass.isAssignableFrom(it) }
        return if (otherArgs == null) {
            viewModelClass
                .getConstructor(dataRepo.javaClass)
                .newInstance(dataRepo)
        } else {
            viewModelClass
                .getConstructor(dataRepo.javaClass, otherArgs.javaClass)
                .newInstance(dataRepo, otherArgs)
        } as T
    }
}
