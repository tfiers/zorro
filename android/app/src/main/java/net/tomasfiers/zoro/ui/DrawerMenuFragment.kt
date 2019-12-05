package net.tomasfiers.zoro.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.tomasfiers.zoro.R

/**
 * A simple [Fragment] subclass.
 */
class DrawerMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.drawer_menu_fragment, container, false)
    }


}
