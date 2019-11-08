package net.tomasfiers.sharetozotero

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SelectCollection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_collection)
        val textfield = findViewById<TextView>(R.id.textfield)
        if (intent?.action == Intent.ACTION_SEND) {
            // We got here via a share menu.
            textfield.text = intent.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}
