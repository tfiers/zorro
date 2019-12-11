package net.tomasfiers.zorro.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.databinding.DrawerMenuFragmentBinding
import net.tomasfiers.zorro.viewmodels.DrawerMenuViewModel
import net.tomasfiers.zorro.viewmodels.DrawerMenuViewModelFactory

class DrawerMenuFragment : Fragment() {

    private val viewModel: DrawerMenuViewModel by viewModels {
        DrawerMenuViewModelFactory((activity?.application as ZorroApplication).dataRepo)
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
