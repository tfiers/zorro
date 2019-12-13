package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingListFragmentBinding
import net.tomasfiers.zorro.viewmodels.BrowsingListViewModel
import net.tomasfiers.zorro.viewmodels.BrowsingListViewModelArgs
import net.tomasfiers.zorro.viewmodels.ZorroViewModelFactory

class BrowsingListFragment : Fragment() {

    private lateinit var binding: BrowsingListFragmentBinding
    private val navigationArgs: BrowsingListFragmentArgs by navArgs()
    private val viewModel: BrowsingListViewModel by viewModels {
        ZorroViewModelFactory(dataRepo, BrowsingListViewModelArgs(navigationArgs.collectionKey))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BrowsingListFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        setupRecyclerView()
        setupPullToRefresh()
        return binding.root
    }

    private fun setupRecyclerView() {
        val collectionClickListener = ListItemClickListener { collection ->
            findNavController().navigate(
                BrowsingListFragmentDirections.actionNavigateIntoCollection(collection.key)
            )
        }
        val itemClickListener = ListItemClickListener { item ->
            Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(
                BrowsingContainerFragmentDirections.actionBrowsingToShowItem(item.key)
            )
        }
        val adapter = RecyclerViewAdapter(collectionClickListener, itemClickListener)
        binding.recyclerView.adapter = adapter

        viewModel.listItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            binding.recyclerView.smoothScrollToPosition(0)
        })
        // Performance improvement (because changes in list content do not change layout size):
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun setupPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener { viewModel.syncLibrary() }
        viewModel.isSyncing.observe(viewLifecycleOwner, Observer { isSyncing ->
            binding.pullToRefresh.isRefreshing = isSyncing
        })
    }
}
