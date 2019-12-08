package net.tomasfiers.zoro.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.R
import net.tomasfiers.zoro.ZoroApplication
import net.tomasfiers.zoro.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        // Set toolbar to act as activity's action bar
        setSupportActionBar(binding.toolbar)

        // Connect toolbar to drawerLayout
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.close_drawer,
            R.string.open_drawer
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Keep screen on for development ease.
        window.addFlags(FLAG_KEEP_SCREEN_ON)

        var snackbar: Snackbar? = null
        val repository = (application as ZoroApplication).dataRepo
        repository.syncError.observe(this, Observer { error ->
            when (error) {
                null -> snackbar?.dismiss()
                else -> {
                    // We must create a new Snackbar each time (instead of reusing one), to play
                    // nicely with the queue of snackbars and its fade in/out effects.
                    snackbar = Snackbar
                        .make(binding.coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss") { /* Clicking on action auto-dismisses. */ }
                        .setText("Synching error (${error})")
                    snackbar!!.show()
                }
            }
        })
        lifecycleScope.launch { repository.syncLibrary() }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        actionBarDrawerToggle.syncState()
    }
}