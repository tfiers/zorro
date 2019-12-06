package net.tomasfiers.zoro.data

import android.app.Application
import android.content.Context

const val INITIAL_LOCAL_LIBRARY_VERSION = 0

class KeyValStore(application: Application) {

    private val store = application.getSharedPreferences(
        "net.tomasfiers.zoro.MyKeyValStore", Context.MODE_PRIVATE
    )

    private enum class Keys {
        LOCAL_LIBRARY_VERSION
    }

    var localLibraryVersion: Int
        get() = store.getInt(Keys.LOCAL_LIBRARY_VERSION.toString(), INITIAL_LOCAL_LIBRARY_VERSION)
        set(value) = with(store.edit()) {
            putInt(Keys.LOCAL_LIBRARY_VERSION.toString(), value)
            apply()
        }
}
