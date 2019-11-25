package net.tomasfiers.zoro

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import net.tomasfiers.zoro.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: MainActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.main_activity)
        // Keep screen on for development ease.
        window.addFlags(FLAG_KEEP_SCREEN_ON)
        var snackbar: Snackbar? = null
        (application as ZoroApplication).repository.syncError.observe(this, Observer { error ->
            when (error) {
                null -> snackbar?.dismiss()
                else -> {
                    // We must create a new Snackbar each time (instead of reusing one), to play
                    // nicely with the queue of snackbars and its fade in/out effects.
                    snackbar = Snackbar
                        .make(binding.coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss") { /* Clicking on action auto-dismisses. */ }
                        .setText(error)
                    snackbar!!.show()
                }
            }
        })
    }
}
