package net.tomasfiers.sharetozotero


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import net.tomasfiers.sharetozotero.databinding.FragmentCollectionBinding

class CollectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCollectionBinding>(
            inflater,
            R.layout.fragment_collection_list,
            container,
            false
        )
        val slide = { view: View ->
            ErrorDialog("yo").show(activity!!.supportFragmentManager, "yo")
            view.findNavController().navigate(R.id.action_collectionListFragment_self)
        }
        binding.collection.setOnClickListener(slide)
        binding.collectionName.setOnClickListener(slide)
        return binding.root
    }
}
