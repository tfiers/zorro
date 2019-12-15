package net.tomasfiers.zorro.ui.browsing.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingItemdetailContainerFragmentBinding

class ContainerFragment(val itemKey: String) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingItemdetailContainerFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val itemLiveData = dataRepo.database.item.get(itemKey)
        itemLiveData.observe(this, Observer { item ->
            binding.linearLayout.removeAllViews()
            item.fieldValues.forEach() { itemFieldValue ->
                val fragmentContainer = FrameLayout(requireContext())
                fragmentContainer.id = View.generateViewId()
                val field = item.fields.find { it.name == itemFieldValue.fieldName }!!
                val fieldFragment = FieldFragment(field.friendlyName, itemFieldValue.value)
                childFragmentManager
                    .beginTransaction()
                    .add(fragmentContainer.id, fieldFragment, null)
                    .commit()
                binding.linearLayout.addView(fragmentContainer)
            }
        })
        return binding.root
    }
}
