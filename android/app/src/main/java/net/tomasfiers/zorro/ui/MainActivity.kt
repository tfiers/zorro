package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.BuildConfig
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.databinding.MainActivityBinding
import net.tomasfiers.zorro.sync.syncLibrary
import net.tomasfiers.zorro.viewmodels.MainActivityViewModel
import net.tomasfiers.zorro.viewmodels.MainActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var dataRepo: DataRepo
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((application as ZorroApplication).dataRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        dataRepo = (application as ZorroApplication).dataRepo
        setupToolbar()
        observeSyncErrors()
        lifecycleScope.launch { dataRepo.syncLibrary() }
        if (BuildConfig.DEBUG) {
            // Keep screen on for development ease.
            window.addFlags(FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun observeSyncErrors() {
        var snackbar: Snackbar? = null
        dataRepo.syncError.observe(this, Observer { error ->
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
    }

    private fun setupToolbar() {
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
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        actionBarDrawerToggle.syncState()
    }
}
