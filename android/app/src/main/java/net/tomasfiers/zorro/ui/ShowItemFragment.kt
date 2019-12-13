package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.ShowItemFragmentBinding
import net.tomasfiers.zorro.viewmodels.ShowItemViewModel
import net.tomasfiers.zorro.viewmodels.ShowItemViewModelArgs
import net.tomasfiers.zorro.viewmodels.ZorroViewModelFactory

class ShowItemFragment : Fragment() {

    private val navigationArgs: ShowItemFragmentArgs by navArgs()
    private val viewModel: ShowItemViewModel by viewModels {
        ZorroViewModelFactory(dataRepo, ShowItemViewModelArgs(navigationArgs.itemKey))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ShowItemFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }
}
