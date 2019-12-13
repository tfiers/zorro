package net.tomasfiers.zorro.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.R
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.databinding.MainActivityBinding
import net.tomasfiers.zorro.sync.syncLibrary

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: MainActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.main_activity)
        binding.lifecycleOwner = this
        val dataRepo = (application as ZorroApplication).dataRepo
        lifecycleScope.launch { dataRepo.syncLibrary() }
    }
}
