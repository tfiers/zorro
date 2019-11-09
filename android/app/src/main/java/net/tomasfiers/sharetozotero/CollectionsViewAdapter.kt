package net.tomasfiers.sharetozotero

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class CollectionsViewAdapter(private val collections: MutableList<JSONObject>) :
    RecyclerView.Adapter<CollectionsViewAdapter.MyViewHolder>() {
    class MyViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView = v.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_in_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = (collections[position]["data"] as JSONObject)["name"] as String
        holder.textView.text = name
    }

    override fun getItemCount() = collections.size
}