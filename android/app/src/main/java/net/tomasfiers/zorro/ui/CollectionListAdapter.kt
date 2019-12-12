package net.tomasfiers.zorro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.data.entities.ListItem
import net.tomasfiers.zorro.databinding.CollectionListItemBinding

class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.key == newItem.key

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.name == newItem.name
}

// One container in the RecyclerView list.
class ListItemViewHolder private constructor(private val binding: CollectionListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(listItem: ListItem, clickListener: ListItemClickListener) {
        binding.item = listItem
        binding.clickListener = clickListener
        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ListItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CollectionListItemBinding.inflate(layoutInflater, parent, false)
            return ListItemViewHolder(binding)
        }
    }
}

// This verbose wrapper class is necessary because `[layout].xml > data > variable` only accepts a
// class, not a function type. It also can only reference a method, not a functional property 👎
class ListItemClickListener(val clickListener: (listItem: ListItem) -> Unit) {
    fun onClick(listItem: ListItem) = clickListener(listItem)
}

class RecyclerViewAdapter(private val clickListener: ListItemClickListener) :
    ListAdapter<ListItem, ListItemViewHolder>(ListItemDiffCallback()) {

    // Called when the RecyclerView requests a new container to add to the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListItemViewHolder.from(parent)

    // Called when the RecyclerView wants to fill a container with a concrete item.
    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}
