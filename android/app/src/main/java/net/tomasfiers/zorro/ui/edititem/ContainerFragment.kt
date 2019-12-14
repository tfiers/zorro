package net.tomasfiers.zorro.ui.edititem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.EdititemContainerFragmentBinding
import net.tomasfiers.zorro.util.ZorroViewModelFactory

class ContainerFragment : Fragment() {

    private val navigationArgs: ContainerFragmentArgs by navArgs()
    private val viewModel: ContainerViewModel by viewModels {
        ZorroViewModelFactory(
            ContainerViewModel::class.java,
            dataRepo,
            ContainerViewModelArgs(navigationArgs.itemKey)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = EdititemContainerFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }
}
