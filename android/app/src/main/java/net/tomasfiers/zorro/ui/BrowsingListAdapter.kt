package net.tomasfiers.zorro.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.data.entities.Collection
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.data.entities.ListItem

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

class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.key == newItem.key

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.name == newItem.name
}

// This verbose wrapper class is necessary because `[layout].xml > data > variable` only accepts a
// class, not a function type. It also can only reference a method, not a functional property 👎
class ListItemClickListener(val clickListener: (listItem: ListItem) -> Unit) {
    fun onClick(listItem: ListItem) = clickListener(listItem)
}
