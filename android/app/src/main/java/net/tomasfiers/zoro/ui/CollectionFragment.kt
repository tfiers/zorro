package net.tomasfiers.zoro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import net.tomasfiers.zoro.MainActivity
import net.tomasfiers.zoro.databinding.CollectionFragmentBinding
import net.tomasfiers.zoro.viewmodels.CollectionViewModel
import net.tomasfiers.zoro.viewmodels.CollectionViewModelFactory

class CollectionFragment : Fragment() {

    private val navigationArgs: CollectionFragmentArgs by navArgs()
    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
            navigationArgs.collectionId,
            (activity as MainActivity).repository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CollectionFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        val adapter = RecyclerViewAdapter(TreeItemClickListener { treeItem ->
            findNavController().navigate(
                CollectionFragmentDirections.actionCollectionSelf(treeItem.id)
            )
        })
        binding.recyclerView.adapter = adapter
        viewModel.collections.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            binding.recyclerView.smoothScrollToPosition(0)
        })
        // Performance improvement (because changes in list content do not change layout size):
        binding.recyclerView.setHasFixedSize(true)
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.onPulledToRefresh()
            binding.pullToRefresh.isRefreshing = false
            // We'll use our own indicator.
        }
        return binding.root
    }
}
