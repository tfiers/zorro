package net.tomasfiers.sharetozotero

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

const val STEPSIZE = 100

class MainActivity : AppCompatActivity() {

    lateinit var queue : RequestQueue
    lateinit var textView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        queue = Volley.newRequestQueue(this.applicationContext)
        textView = findViewById<TextView>(R.id.textView)
        fetchUntilDone()
    }

    private val collections = mutableListOf<JSONObject>()
    private var start = 0

    private fun fetchUntilDone() {
        val request = ZoteroAPIRequest("/collections?limit=$STEPSIZE&start=$start",
            Response.Listener { response ->
                for (i in 0 until response.data.length()) {
                    collections.add(response.data[i] as JSONObject)
                }
                start += 100
                if (start > response.headers["Total-Results"]?.toInt() ?: 0) {
                    displayCollections()
                } else {
                    fetchUntilDone()
                }
            },
            Response.ErrorListener {
                textView.text = "Downloading Zotero collections failed. (Info for nerds: $it)"
            })
        queue.add(request)
    }

    private fun displayCollections() {
//        textView.text = (collections[0] as JSONObject)["data"].toString()
        textView.text = collections.size.toString()
    }
}

data class ZoteroAPIResponse(val data: JSONArray, val headers: Map<String, String>)

class ZoteroAPIRequest(
    endpoint: String,
    private val listener: Response.Listener<ZoteroAPIResponse>,
    errorListener: Response.ErrorListener
) : Request<ZoteroAPIResponse>(
    Method.GET,
    "https://api.zotero.org/users/4670453$endpoint",
    errorListener
) {
    override fun getHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        headers["Zotero-API-Version"] = "3"
        headers["Zotero-API-Key"] = ZOTERO_API_KEY
        return headers
    }

    override fun deliverResponse(response: ZoteroAPIResponse) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ZoteroAPIResponse> {
        return try {
            val charset = Charset.forName(HttpHeaderParser.parseCharset(response?.headers, "utf-8"))
            val jsonString = String(response?.data ?: ByteArray(0), charset)
            Response.success(
                ZoteroAPIResponse(
                    data = JSONArray(jsonString),
                    headers = response?.headers ?: mapOf()
                ), HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JSONException) {
            Response.error(ParseError(e))
        }
    }
}
