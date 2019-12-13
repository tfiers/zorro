package net.tomasfiers.zorro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.data.entities.Collection
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.data.entities.ListItem
import net.tomasfiers.zorro.databinding.BrowsingListItemCollectionBinding
import net.tomasfiers.zorro.databinding.BrowsingListItemItemBinding

class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.key == newItem.key

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.name == newItem.name
}

// One container in the RecyclerView list.
class BrowsingListItemCollectionViewHolder private constructor(private val binding: BrowsingListItemCollectionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(collection: Collection, clickListener: ListItemClickListener) {
        binding.collection = collection
        binding.clickListener = clickListener
        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): BrowsingListItemCollectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BrowsingListItemCollectionBinding.inflate(layoutInflater, parent, false)
            return BrowsingListItemCollectionViewHolder(binding)
        }
    }
}

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

// This verbose wrapper class is necessary because `[layout].xml > data > variable` only accepts a
// class, not a function type. It also can only reference a method, not a functional property 👎
class ListItemClickListener(val clickListener: (listItem: ListItem) -> Unit) {
    fun onClick(listItem: ListItem) = clickListener(listItem)
}

class RecyclerViewAdapter(
    private val collectionClickListener: ListItemClickListener,
    private val itemClickListener: ListItemClickListener
) :
    ListAdapter<ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    private enum class ViewHolderTypes { COLLECTION, ITEM }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Collection -> ViewHolderTypes.COLLECTION.ordinal
        is ItemWithReferences -> ViewHolderTypes.ITEM.ordinal
        else -> throw TypeCastException()
    }

    // Called when the RecyclerView requests a new container to add to the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ViewHolderTypes.COLLECTION.ordinal -> BrowsingListItemCollectionViewHolder.from(parent)
        ViewHolderTypes.ITEM.ordinal -> BrowsingListItemItemViewHolder.from(parent)
        else -> throw TypeCastException()
    }

    // Called when the RecyclerView wants to fill a container with a concrete item.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = getItem(position)
        if (listItem is Collection) {
            (holder as BrowsingListItemCollectionViewHolder)
                .bind(listItem, collectionClickListener)
        } else if (listItem is ItemWithReferences)
            (holder as BrowsingListItemItemViewHolder)
                .bind(listItem, itemClickListener)
    }
}