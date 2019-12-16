package net.tomasfiers.zorro.ui.browsing.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.tomasfiers.zorro.data.entities.Collection
import net.tomasfiers.zorro.databinding.BrowsingListElementCollectionBinding


// One container in the RecyclerView list.
class RecyclerElementCollectionViewHolder
private constructor(private val binding: BrowsingListElementCollectionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(collection: Collection, clickListener: ListElementClicklistener) {
        binding.collection = collection
        binding.clickListener = clickListener
        // (A recommended slight speed optimization:)
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): RecyclerElementCollectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                BrowsingListElementCollectionBinding.inflate(layoutInflater, parent, false)
            return RecyclerElementCollectionViewHolder(
                binding
            )
        }
    }
}
