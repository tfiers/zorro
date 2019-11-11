package net.tomasfiers.sharetozotero

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import net.tomasfiers.sharetozotero.databinding.FragmentCollectionsBinding
import org.json.JSONObject

const val ITEMS_PER_REQUEST = 100

class CollectionsFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val collections = mutableListOf<JSONObject>()
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCollectionsBinding>(
            inflater,
            R.layout.fragment_collections,
            container,
            false
        )

        viewManager = LinearLayoutManager(activity)
        viewAdapter = CollectionsViewAdapter(collections)
        binding.myRecyclerView.setHasFixedSize(true)
        binding.myRecyclerView.layoutManager = viewManager
        binding.myRecyclerView.adapter = viewAdapter

        requestQueue = Volley.newRequestQueue(activity)
        fetchUntilDone()

        return binding.root
    }

    private var startIndex = 0

    private fun fetchUntilDone() {
        val request = ZoteroAPIRequest("/collections?limit=$ITEMS_PER_REQUEST&start=$startIndex",
            Response.Listener { response ->
                for (i in 0 until response.data.length()) {
                    collections.add(response.data[i] as JSONObject)
                    viewAdapter.notifyItemInserted(collections.size - 1)
                }
                startIndex += ITEMS_PER_REQUEST
                if (startIndex < response.headers["Total-Results"]?.toInt() ?: 0) {
                    fetchUntilDone()
                }
            },
            Response.ErrorListener { err ->
                val message = "Downloading Zotero collections failed.\n\n(Info for nerds: $err)"
                ErrorDialog(message).show(activity!!.supportFragmentManager, message)
            })
        requestQueue.add(request)
    }
}
