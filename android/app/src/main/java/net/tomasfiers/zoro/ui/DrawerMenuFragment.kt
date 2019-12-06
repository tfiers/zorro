package net.tomasfiers.zoro.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.tomasfiers.zoro.ZoroApplication
import net.tomasfiers.zoro.databinding.DrawerMenuFragmentBinding
import net.tomasfiers.zoro.viewmodels.DrawerMenuViewModel
import net.tomasfiers.zoro.viewmodels.DrawerMenuViewModelFactory

class DrawerMenuFragment : Fragment() {

    private val viewModel: DrawerMenuViewModel by viewModels {
        DrawerMenuViewModelFactory(activity?.application as ZoroApplication)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DrawerMenuFragmentBinding.inflate(inflater, container, false)
        // The following is necessary so that LiveData changes get propagated to the UI.
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }
}
