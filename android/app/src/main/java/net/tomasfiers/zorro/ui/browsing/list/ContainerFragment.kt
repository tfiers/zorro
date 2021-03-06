package net.tomasfiers.zorro.ui.browsing.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.getPersistentValue
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingListContainerFragmentBinding
import net.tomasfiers.zorro.util.ZorroViewModelFactory
import net.tomasfiers.zorro.ui.browsing.itemdetail.ContainerFragment as ItemdetailContainerFragment

class ContainerFragment : Fragment() {

    private lateinit var binding: BrowsingListContainerFragmentBinding
    private val navigationArgs: ContainerFragmentArgs by navArgs()
    private val viewModel: ContainerViewModel by viewModels {
        ZorroViewModelFactory(
            ContainerViewModel::class.java,
            dataRepo,
            ContainerViewModelArgs(navigationArgs.collectionKey)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BrowsingListContainerFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        setupRecyclerView()
        setupPullToRefresh()
        autoNavIntoSubcollectionAtStartup()
        return binding.root
    }

    private fun setupRecyclerView() {
        val collectionClickListener = ListElementClicklistener { collection ->
            navigateIntoCollection(collection.key)
        }
        val itemClickListener = ListElementClicklistener { item ->
            val itemDetailFragment = ItemdetailContainerFragment(item.key)
            val fragmentManager = requireActivity().supportFragmentManager
            itemDetailFragment.show(fragmentManager, null)
        }
        val adapter = RecyclerViewAdapter(collectionClickListener, itemClickListener)
        binding.recyclerView.adapter = adapter

        viewModel.listElements.observe(viewLifecycleOwner, Observer {
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

    private fun autoNavIntoSubcollectionAtStartup() = lifecycleScope.launch {
        if (dataRepo.getPersistentValue(Key.DEVELOPER_MODE) && !dataRepo.autoNavigatedToSubcollection) {
            dataRepo.autoNavigatedToSubcollection = true
            navigateIntoCollection("838WYWGX")
        }
    }

    private fun navigateIntoCollection(collectionKey: String?) =
        findNavController().navigate(
            ContainerFragmentDirections.actionNavigateIntoCollection(collectionKey)
        )
}
