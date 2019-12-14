package net.tomasfiers.zorro.ui.browsing.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.databinding.BrowsingListElementItemBinding

// One container in the RecyclerView list.
class ItemElementViewHolder private constructor(private val binding: BrowsingListElementItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ItemWithReferences, clickListener: ListElementClicklistener) {
        binding.item = item
        binding.clickListener = clickListener

        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }


    companion object {
        fun from(parent: ViewGroup): ItemElementViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BrowsingListElementItemBinding.inflate(layoutInflater, parent, false)
            return ItemElementViewHolder(
                binding
            )
        }
    }
}

@BindingAdapter("drawableForItem")
fun setItemDrawable(imageView: ImageView, item: ItemWithReferences) {
    val drawableName = "list_item__${camelToSnakeCase(
        item.item.itemTypeName
    )}"
    var drawableId = imageView.resources.getIdentifier(
        drawableName, "drawable", "net.tomasfiers.zorro"
    )
    if (drawableId == 0) {
        drawableId = R.drawable.list_item__item
    }
    imageView.setImageResource(drawableId)
}

private fun camelToSnakeCase(string: String): String =
    string.toList().joinToString("") { char ->
        if (char.isUpperCase())
            "_${char.toLowerCase()}"
        else "$char"
    }
