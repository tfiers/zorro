package net.tomasfiers.zoro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zoro.data.ListItem
import net.tomasfiers.zoro.databinding.ListItemBinding

class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.name == newItem.name
}

// One container in the RecyclerView list.
class MyViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(listItem: ListItem) {
        binding.item = listItem
        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }
}

class RecyclerViewAdapter : ListAdapter<ListItem, MyViewHolder>(ListItemDiffCallback()) {

    // Called when the RecyclerView requests a new container to add to the list.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    // Called when the RecyclerView wants to fill a container with a concrete item.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
