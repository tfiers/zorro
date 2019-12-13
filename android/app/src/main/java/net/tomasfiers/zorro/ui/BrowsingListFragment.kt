package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.databinding.BrowsingListFragmentBinding
import net.tomasfiers.zorro.viewmodels.BrowsingListViewModel
import net.tomasfiers.zorro.viewmodels.BrowsingListViewModelFactory
import timber.log.Timber

class BrowsingListFragment : Fragment() {

    private val navigationArgs: BrowsingListFragmentArgs by navArgs()
    private val viewModel: BrowsingListViewModel by viewModels {
        BrowsingListViewModelFactory(
            navigationArgs.collectionKey,
            (activity?.application as ZorroApplication).dataRepo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = BrowsingListFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        setupRecyclerView(binding)
        setupPullToRefresh(binding)
        //viewModel.collectionName.observe(viewLifecycleOwner, Observer { collectionName ->
        //    (activity as MainActivity).binding.toolbar.title = collectionName
        //})
        return binding.root
    }

    private fun setupRecyclerView(binding: BrowsingListFragmentBinding) {
        val collectionClickListener = ListItemClickListener { collection ->
            findNavController().navigate(
                BrowsingListFragmentDirections.actionNavigateIntoCollection(collection.key)
            )
        }
        val itemClickListener = ListItemClickListener { item ->
            Timber.i("Show item ${item.name}")
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

    private fun setupPullToRefresh(binding: BrowsingListFragmentBinding) {
        binding.pullToRefresh.setOnRefreshListener { viewModel.syncLibrary() }
        viewModel.isSyncing.observe(viewLifecycleOwner, Observer { isSyncing ->
            binding.pullToRefresh.isRefreshing = isSyncing
        })
    }
}
