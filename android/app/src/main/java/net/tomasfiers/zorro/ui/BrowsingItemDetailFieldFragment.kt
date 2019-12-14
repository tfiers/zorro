package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.databinding.BrowsingItemDetailFieldFragmentBinding

class BrowsingItemDetailFieldFragment(item: LiveData<ItemWithReferences>) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingItemDetailFieldFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}
