package net.tomasfiers.zorro.ui.browsing


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingDrawerMenuFragmentBinding
import net.tomasfiers.zorro.util.ZorroViewModelFactory

class DrawerMenuFragment : Fragment() {

    private val viewModel: DrawerMenuViewModel by viewModels {
        ZorroViewModelFactory(DrawerMenuViewModel::class.java, dataRepo, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingDrawerMenuFragmentBinding.inflate(inflater, container, false)
        // The following is necessary so that LiveData changes get propagated to the UI.
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }
}
