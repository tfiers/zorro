package net.tomasfiers.zorro.ui.browsing.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.tomasfiers.zorro.databinding.BrowsingItemdetailFieldFragmentBinding

class FieldFragment(
    private val fieldNameFriendly: String,
    private val fieldValue: String
) :
    Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingItemdetailFieldFragmentBinding.inflate(inflater, container, false)
        binding.fieldNameFriendly = fieldNameFriendly
        binding.fieldValue = fieldValue
        return binding.root
    }
}
