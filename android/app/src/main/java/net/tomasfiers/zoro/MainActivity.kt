package net.tomasfiers.zoro

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.main_activity)

        navController = findNavController(R.id.nav_host_fragment)
        drawerLayout = binding.drawerLayout
        setupActionBarWithNavController(navController, drawerLayout)
        setupWithNavController(binding.navView, navController)

        // Keep screen on for development ease.
        window.addFlags(FLAG_KEEP_SCREEN_ON)

        var snackbar: Snackbar? = null
        val repository = (application as ZoroApplication).repository
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
        lifecycleScope.launch { repository.syncCollections() }
    }

    override fun onSupportNavigateUp() = navigateUp(navController, drawerLayout)

}
