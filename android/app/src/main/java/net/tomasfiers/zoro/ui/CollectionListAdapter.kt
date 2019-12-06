package net.tomasfiers.zoro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zoro.data.domain.TreeItem
import net.tomasfiers.zoro.databinding.TreeItemBinding

class ListItemDiffCallback : DiffUtil.ItemCallback<TreeItem>() {
    override fun areItemsTheSame(oldItem: TreeItem, newItem: TreeItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TreeItem, newItem: TreeItem) =
        oldItem.name == newItem.name
}

// One container in the RecyclerView list.
class TreeItemViewHolder private constructor(private val binding: TreeItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(treeItem: TreeItem, clickListener: TreeItemClickListener) {
        binding.item = treeItem
        binding.clickListener = clickListener
        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): TreeItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TreeItemBinding.inflate(layoutInflater, parent, false)
            return TreeItemViewHolder(binding)
        }
    }
}

// This verbose wrapper class is necessary because `tree_item.xml > data > variable` only accepts a
// class, not a function type. It also can't reference a function property 👎
class TreeItemClickListener(val clickListener: (treeItem: TreeItem) -> Unit) {
    fun onClick(treeItem: TreeItem) = clickListener(treeItem)
}

class RecyclerViewAdapter(private val clickListener: TreeItemClickListener) :
    ListAdapter<TreeItem, TreeItemViewHolder>(ListItemDiffCallback()) {

    // Called when the RecyclerView requests a new container to add to the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TreeItemViewHolder.from(parent)

    // Called when the RecyclerView wants to fill a container with a concrete item.
    override fun onBindViewHolder(holder: TreeItemViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}
