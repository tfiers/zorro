package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.databinding.BrowsingContainerFragmentBinding
import net.tomasfiers.zorro.sync.syncLibrary
import net.tomasfiers.zorro.viewmodels.BrowsingContainerViewModel
import net.tomasfiers.zorro.viewmodels.BrowsingContainerViewModelFactory

class BrowsingContainerFragment : Fragment() {

    private lateinit var binding: BrowsingContainerFragmentBinding
    private lateinit var dataRepo: DataRepo
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private val viewModel: BrowsingContainerViewModel by viewModels {
        BrowsingContainerViewModelFactory((requireActivity().application as ZorroApplication).dataRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BrowsingContainerFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        dataRepo = (requireActivity().application as ZorroApplication).dataRepo
        setupToolbar()
        observeSyncErrors()
        lifecycleScope.launch { dataRepo.syncLibrary() }
        return binding.root
    }

    private fun setupToolbar() {
        // Connect toolbar to drawerLayout
        actionBarDrawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.toolbar,
            R.string.close_drawer,
            R.string.open_drawer
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun observeSyncErrors() {
        var snackbar: Snackbar? = null
        dataRepo.syncError.observe(this, Observer { error ->
            when (error) {
                null -> snackbar?.dismiss()
                else -> {
                    // We must create a new Snackbar each time (instead of reusing one), to play
                    // nicely with the queue of snackbars and its fade in/out effects.
                    snackbar = Snackbar
                        .make(binding.coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss") { /* Clicking on action auto-dismisses. */ }
                        .setText("Synching error (${error})")
                    snackbar!!.show()
                }
            }
        })
    }
}
