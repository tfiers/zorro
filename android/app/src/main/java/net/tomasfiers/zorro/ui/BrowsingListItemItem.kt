package net.tomasfiers.zorro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.databinding.BrowsingListItemItemBinding

// One container in the RecyclerView list.
class BrowsingListItemItemViewHolder private constructor(private val binding: BrowsingListItemItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ItemWithReferences, clickListener: ListItemClickListener) {
        binding.item = item
        binding.clickListener = clickListener

        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }


    companion object {
        fun from(parent: ViewGroup): BrowsingListItemItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BrowsingListItemItemBinding.inflate(layoutInflater, parent, false)
            return BrowsingListItemItemViewHolder(binding)
        }
    }
}

@BindingAdapter("drawableForItem")
fun setItemDrawable(imageView: ImageView, item: ItemWithReferences) {
    val drawableName = "list_item__${camelToSnakeCase(item.item.itemTypeName)}"
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
