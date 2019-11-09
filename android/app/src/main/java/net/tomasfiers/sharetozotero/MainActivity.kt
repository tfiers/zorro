package net.tomasfiers.sharetozotero

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textView)
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                textView.text = response
            },
            Response.ErrorListener {
                textView.text = "Downloading Zotero collection failed"
            })
        queue.add(request)
    }
}
