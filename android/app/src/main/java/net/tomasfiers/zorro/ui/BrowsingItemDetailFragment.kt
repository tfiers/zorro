package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingItemDetailFragmentBinding
import net.tomasfiers.zorro.viewmodels.BrowsingItemDetailViewModel
import net.tomasfiers.zorro.viewmodels.BrowsingItemDetailViewModelArgs
import net.tomasfiers.zorro.viewmodels.ZorroViewModelFactory

class BrowsingItemDetailFragment(itemKey: String) : BottomSheetDialogFragment() {
    private val viewModel: BrowsingItemDetailViewModel by viewModels {
        ZorroViewModelFactory(
            BrowsingItemDetailViewModel::class.java,
            dataRepo,
            BrowsingItemDetailViewModelArgs(itemKey)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingItemDetailFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }
}
