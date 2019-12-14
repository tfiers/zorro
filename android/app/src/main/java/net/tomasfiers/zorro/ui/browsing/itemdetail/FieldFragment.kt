package net.tomasfiers.zorro.ui.browsing.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.databinding.BrowsingItemdetailFieldFragmentBinding

class FieldFragment(item: LiveData<ItemWithReferences>) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingItemdetailFieldFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}
