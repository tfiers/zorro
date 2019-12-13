package net.tomasfiers.zorro.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.databinding.BrowsingDrawerMenuFragmentBinding
import net.tomasfiers.zorro.viewmodels.BrowsingDrawerMenuViewModel
import net.tomasfiers.zorro.viewmodels.BrowsingDrawerMenuViewModelFactory

class BrowsingDrawerMenuFragment : Fragment() {

    private val viewModel: BrowsingDrawerMenuViewModel by viewModels {
        BrowsingDrawerMenuViewModelFactory((activity?.application as ZorroApplication).dataRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingDrawerMenuFragmentBinding.inflate(inflater, container, false)
        // The following is necessary so that LiveData changes get propagated to the UI.
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }
}