package net.tomasfiers.zoro

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.Repository
import net.tomasfiers.zoro.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {

    val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: MainActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.main_activity)
        // Keep screen on for development ease.
        window.addFlags(FLAG_KEEP_SCREEN_ON)
        val snackbar = Snackbar.make(binding.coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Dismiss") { /* Clicking on action auto-dismisses. */ }
        repository.synchingError.observe(this, Observer { error ->
            when (error) {
                null -> snackbar.dismiss()
                else -> snackbar.setText(error).show()
            }
        })
        lifecycleScope.launch {
            repository.getAllCollections()
        }
    }
}
