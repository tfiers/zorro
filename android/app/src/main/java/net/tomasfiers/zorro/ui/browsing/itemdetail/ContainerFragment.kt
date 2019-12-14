package net.tomasfiers.zorro.ui.browsing.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingItemdetailContainerFragmentBinding
import net.tomasfiers.zorro.util.ZorroViewModelFactory

class ContainerFragment(itemKey: String) : BottomSheetDialogFragment() {
    private val viewModel: BrowsingItemDetailViewModel by viewModels {
        ZorroViewModelFactory(
            BrowsingItemDetailViewModel::class.java,
            dataRepo,
            BrowsingItemDetailViewModelArgs(
                itemKey
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingItemdetailContainerFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        //binding.linearLayout.addView()
        return binding.root
    }
}
