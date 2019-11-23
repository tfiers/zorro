package net.tomasfiers.zoro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zoro.data.TreeItem
import net.tomasfiers.zoro.databinding.TreeItemBinding

class ListItemDiffCallback : DiffUtil.ItemCallback<TreeItem>() {
    override fun areItemsTheSame(oldItem: TreeItem, newItem: TreeItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: TreeItem, newItem: TreeItem) =
        oldItem.name == newItem.name
}

// One container in the RecyclerView list.
class MyViewHolder(private val binding: TreeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(treeItem: TreeItem) {
        binding.item = treeItem
        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }
}

class RecyclerViewAdapter : ListAdapter<TreeItem, MyViewHolder>(ListItemDiffCallback()) {

    // Called when the RecyclerView requests a new container to add to the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TreeItemBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    // Called when the RecyclerView wants to fill a container with a concrete item.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
