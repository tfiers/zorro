package net.tomasfiers.zoro.data

import android.app.Application
import android.content.Context

const val INITIAL_LOCAL_LIBRARY_VERSION = 0

class KeyValStore(application: Application) {

    private val store = application.getSharedPreferences(
        "net.tomasfiers.zoro.MyKeyValStore", Context.MODE_PRIVATE
    )

    private enum class Keys {
        LOCAL_LIBRARY_VERSION,
        LOCAL_SCHEMA_ETAG
    }

    var localLibraryVersion: Int
        get() = store.getInt(Keys.LOCAL_LIBRARY_VERSION.name, INITIAL_LOCAL_LIBRARY_VERSION)
        set(value) = with(store.edit()) {
            putInt(Keys.LOCAL_LIBRARY_VERSION.name, value)
            apply()
        }

    var localSchemaETag: String?
        get() = store.getString(Keys.LOCAL_SCHEMA_ETAG.name, null)
        set(value) = with(store.edit()) {
            putString(Keys.LOCAL_SCHEMA_ETAG.name, value)
            apply()
        }
}
