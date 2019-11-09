package net.tomasfiers.sharetozotero

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject

const val STEPSIZE = 100

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val collections = mutableListOf<JSONObject>()
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = CollectionsViewAdapter(collections)
        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        requestQueue = Volley.newRequestQueue(this)
        fetchUntilDone()
    }

    private var start = 0

    private fun fetchUntilDone() {
        val request = ZoteroAPIRequest("/collections?limit=$STEPSIZE&start=$start",
            Response.Listener { response ->
                for (i in 0 until response.data.length()) {
                    collections.add(response.data[i] as JSONObject)
                    viewAdapter.notifyItemInserted(collections.size - 1)
                }
                start += STEPSIZE
                if (start < response.headers["Total-Results"]?.toInt() ?: 0) {
                    fetchUntilDone()
                }
            },
            Response.ErrorListener { err ->
                val message = "Downloading Zotero collections failed.\n\n(Info for nerds: $err)"
                ErrorDialog(message).show(supportFragmentManager, message)
            })
        requestQueue.add(request)
    }
}
