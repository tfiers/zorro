package net.tomasfiers.zoro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zoro.data.TreeItem
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
    fun bind(treeItem: TreeItem) {
        binding.item = treeItem
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

class RecyclerViewAdapter : ListAdapter<TreeItem, TreeItemViewHolder>(ListItemDiffCallback()) {

    // Called when the RecyclerView requests a new container to add to the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TreeItemViewHolder.from(parent)

    // Called when the RecyclerView wants to fill a container with a concrete item.
    override fun onBindViewHolder(holder: TreeItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
