package net.tomasfiers.zorro.ui.browsing.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.databinding.BrowsingItemdetailFieldFragmentBinding

data class FieldData(val label: String, val value: String)

open class FieldFragment(private val data: FieldData) : Fragment() {

    open val valueTextStyle = R.style.TextAppearance_Zorro_ItemDetailValue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = BrowsingItemdetailFieldFragmentBinding.inflate(inflater, container, false)
        binding.label = data.label
        binding.value = data.value
        TextViewCompat.setTextAppearance(binding.valueText, valueTextStyle)
        return binding.root
    }
}

class TitleFieldFragment(data: FieldData) : FieldFragment(data) {
    override val valueTextStyle = R.style.TextAppearance_Zorro_ItemDetailValueTitle
}

class AbstractFieldFragment(data: FieldData) : FieldFragment(data) {
    override val valueTextStyle = R.style.TextAppearance_Zorro_ItemDetailValueAbstract
}
