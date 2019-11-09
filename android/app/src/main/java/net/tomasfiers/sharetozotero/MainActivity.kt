package net.tomasfiers.sharetozotero

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val queue = Volley.newRequestQueue(this)
        val request = ZoteroAPIRequest("/collections",
            Response.Listener { displayCollections(it) },
            Response.ErrorListener {
                textView.text = "Downloading Zotero collection failed"
            })
        queue.add(request)
    }

    private fun displayCollections(collections: JSONArray) {
        textView.text = (collections[0] as JSONObject)["data"].toString()
    }
}

class ZoteroAPIRequest(
    endpoint: String, listener: Response.Listener<JSONArray>, errorListener: Response.ErrorListener
) : JsonArrayRequest(
    "https://api.zotero.org/users/4670453$endpoint",
    listener,
    errorListener
) {
    override fun getHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        headers["Zotero-API-Version"] = "3"
        headers["Zotero-API-Key"] = ZOTERO_API_KEY
        return headers
    }
}

