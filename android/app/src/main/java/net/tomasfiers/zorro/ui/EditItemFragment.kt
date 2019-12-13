package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.EditItemFragmentBinding
import net.tomasfiers.zorro.viewmodels.EditItemViewModel
import net.tomasfiers.zorro.viewmodels.EditItemViewModelArgs
import net.tomasfiers.zorro.viewmodels.ZorroViewModelFactory

class EditItemFragment : Fragment() {

    private val navigationArgs: EditItemFragmentArgs by navArgs()
    private val viewModel: EditItemViewModel by viewModels {
        ZorroViewModelFactory(dataRepo, EditItemViewModelArgs(navigationArgs.itemKey))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = EditItemFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }
}
