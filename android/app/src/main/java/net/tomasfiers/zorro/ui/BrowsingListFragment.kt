package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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

    private lateinit var binding: BrowsingListFragmentBinding
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
        binding = BrowsingListFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        setupRecyclerView()
        setupPullToRefresh()
        configureBackButton()
        //viewModel.collectionName.observe(viewLifecycleOwner, Observer { collectionName ->
        //    (activity as MainActivity).binding.toolbar.title = collectionName
        //})
        return binding.root
    }

    private fun configureBackButton() {
        // When we are at the topmost collection and the back button is pressed, quit the app
        // (instead of going to an empty screen in the containing MainActivity).
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (navigationArgs.collectionKey == null) {
                activity!!.finishAffinity()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupRecyclerView() {
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

    private fun setupPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener { viewModel.syncLibrary() }
        viewModel.isSyncing.observe(viewLifecycleOwner, Observer { isSyncing ->
            binding.pullToRefresh.isRefreshing = isSyncing
        })
    }
}
