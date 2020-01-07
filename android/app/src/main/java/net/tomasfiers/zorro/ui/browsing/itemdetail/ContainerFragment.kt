package net.tomasfiers.zorro.ui.browsing.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.tomasfiers.zorro.data.entities.ItemFieldValue
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.dataRepo
import net.tomasfiers.zorro.databinding.BrowsingItemdetailContainerFragmentBinding
import net.tomasfiers.zorro.zotero_api.ABSTRACT
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
            ?.let { makeFieldData(it, item) }
            ?.let { TitleFieldFragment(it) }
            ?.let { addFieldFragmentToLayout(it) }

        item.sortedFieldValues
            .filter { it.fieldName != TITLE }
            .forEach {
                val data = makeFieldData(it, item)
                val fragment = if (it.fieldName == ABSTRACT) {
                    AbstractFieldFragment(data)
                } else {
                    FieldFragment(data)
                }
                addFieldFragmentToLayout(fragment)
            }
    }

    private fun makeFieldData(itemFieldValue: ItemFieldValue, item: ItemWithReferences): FieldData {
        val field = item.fields.find { it.name == itemFieldValue.fieldName }!!
        return FieldData(field.friendlyName, itemFieldValue.value)
    }

    private fun addFieldFragmentToLayout(fieldFragment: Fragment) {
        val fragmentContainer = FrameLayout(requireContext())
        fragmentContainer.id = View.generateViewId()
        childFragmentManager
            .beginTransaction()
            .add(fragmentContainer.id, fieldFragment, null)
            .commit()
        binding.fieldsContainer.addView(fragmentContainer)
    }
}
