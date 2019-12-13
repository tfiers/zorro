package net.tomasfiers.zorro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.data.entities.Collection
import net.tomasfiers.zorro.databinding.BrowsingListItemCollectionBinding


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
