package net.tomasfiers.zoro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import net.tomasfiers.zoro.databinding.CollectionFragmentBinding

class CollectionFragment : Fragment() {

    private val viewModel: CollectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CollectionFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val adapter = RecyclerViewAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.collections.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        // Performance improvement (becauase changes in list content do not change layout size):
        binding.recyclerView.setHasFixedSize(true)

        return binding.root
    }
}
