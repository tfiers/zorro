package net.tomasfiers.zorro.ui.browsing


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingDrawerMenuFragmentBinding
import net.tomasfiers.zorro.util.ZorroViewModelFactory

class DrawerMenuFragment : Fragment() {

    private lateinit var binding: BrowsingDrawerMenuFragmentBinding

    private val viewModel: DrawerMenuViewModel by viewModels {
        ZorroViewModelFactory(DrawerMenuViewModel::class.java, dataRepo, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BrowsingDrawerMenuFragmentBinding.inflate(inflater, container, false)
        // The following is necessary so that LiveData changes get propagated to the UI.
        binding.lifecycleOwner = this
        binding.vm = viewModel
        observeDevModeToggled()
        return binding.root
    }

    private fun observeDevModeToggled() {
        viewModel.developerModeToggled.observe(this, Observer {
            if (it) {
                Snackbar.make(
                    binding.developerModeSwitch,
                    "Restart app for this change to take effect",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }
}
