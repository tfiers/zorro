package net.tomasfiers.zoro

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONArray
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

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
            val charset = Charset.forName(
                HttpHeaderParser.parseCharset(
                    response?.headers,
                    "utf-8"
                )
            )
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