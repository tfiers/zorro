package net.tomasfiers.zorro.ui.browsing.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.data.entities.Collection
import net.tomasfiers.zorro.data.entities.ItemWithReferences
import net.tomasfiers.zorro.data.entities.ListElement

class RecyclerViewAdapter(
    private val collectionClickListener: ListElementClicklistener,
    private val elementClicklistener: ListElementClicklistener
) :
    ListAdapter<ListElement, RecyclerView.ViewHolder>(ListElementDiffCallback()) {

    private enum class ViewHolderTypes { COLLECTION, ITEM }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Collection -> ViewHolderTypes.COLLECTION.ordinal
        is ItemWithReferences -> ViewHolderTypes.ITEM.ordinal
        else -> throw TypeCastException()
    }

    // Called when the RecyclerView requests a new container to add to the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ViewHolderTypes.COLLECTION.ordinal -> CollectionElementViewHolder.from(
            parent
        )
        ViewHolderTypes.ITEM.ordinal -> ItemElementViewHolder.from(
            parent
        )
        else -> throw TypeCastException()
    }

    // Called when the RecyclerView wants to fill a container with a concrete item.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listElement = getItem(position)
        if (listElement is Collection) {
            (holder as CollectionElementViewHolder)
                .bind(listElement, collectionClickListener)
        } else if (listElement is ItemWithReferences)
            (holder as ItemElementViewHolder)
                .bind(listElement, elementClicklistener)
    }
}

class ListElementDiffCallback : DiffUtil.ItemCallback<ListElement>() {
    override fun areItemsTheSame(oldElement: ListElement, newElement: ListElement) =
        oldElement.key == newElement.key

    override fun areContentsTheSame(oldElement: ListElement, newElement: ListElement) =
        oldElement.name == newElement.name
}

// This verbose wrapper class is necessary because `[layout].xml > data > variable` only accepts a
// class, not a function type. It also can only reference a method, not a functional property 👎
class ListElementClicklistener(val clickListener: (listElement: ListElement) -> Unit) {
    fun onClick(listElement: ListElement) = clickListener(listElement)
}
