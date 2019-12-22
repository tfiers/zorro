package net.tomasfiers.zorro.ui.browsing.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.tomasfiers.zorro.data.entities.ItemFieldValue
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingItemdetailContainerFragmentBinding
import net.tomasfiers.zorro.zotero_api.TITLE

class ContainerFragment(val itemKey: String) : BottomSheetDialogFragment() {

    private lateinit var binding: BrowsingItemdetailContainerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BrowsingItemdetailContainerFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val itemLiveData = dataRepo.database.item.get(itemKey)
        itemLiveData.observe(this, Observer { onItemUpdated(it) })
        return binding.root
    }

    private fun onItemUpdated(item: ItemWithReferences) {
        binding.fieldsContainer.removeAllViews()

        item.fieldValues
            .find { it.fieldName == TITLE }
            ?.let { addFieldValueToLayout(it, item) }

        item.sortedFieldValues
            .filter { it.fieldName != TITLE }
            .forEach { addFieldValueToLayout(it, item) }
    }

    private fun addFieldValueToLayout(
        itemFieldValue: ItemFieldValue,
        item: ItemWithReferences
    ) {
        val fragmentContainer = FrameLayout(requireContext())
        fragmentContainer.id = View.generateViewId()
        val field = item.fields.find { it.name == itemFieldValue.fieldName }!!
        val fieldFragment = FieldFragment(field.friendlyName, itemFieldValue.value)
        childFragmentManager
            .beginTransaction()
            .add(fragmentContainer.id, fieldFragment, null)
            .commit()
        binding.fieldsContainer.addView(fragmentContainer)
    }
}
