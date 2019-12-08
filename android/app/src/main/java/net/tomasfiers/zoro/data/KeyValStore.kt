package net.tomasfiers.zoro.data

import android.app.Application
import android.content.Context

const val INITIAL_LOCAL_LIBRARY_VERSION = 0

class KeyValStore(application: Application) {

    private val store = application.getSharedPreferences(
        "net.tomasfiers.zoro.MyKeyValStore", Context.MODE_PRIVATE
    )


    var localLibraryVersion
        get() = get(Key.LOCAL_LIBRARY_VERSION) as Int
        set(value) = set(Key.LOCAL_LIBRARY_VERSION, value)

    var localSchemaETag
        get() = get(Key.LOCAL_SCHEMA_ETAG) as String?
        set(value) = set(Key.LOCAL_SCHEMA_ETAG, value)


    private enum class Key(val valueType: ValueType, val defaultValue: Any?) {
        LOCAL_LIBRARY_VERSION(ValueType.INT, INITIAL_LOCAL_LIBRARY_VERSION),
        LOCAL_SCHEMA_ETAG(ValueType.STRING, null)
    }

    private enum class ValueType { STRING, INT }

    private fun get(key: Key): Any? {
        return when (key.valueType) {
            ValueType.STRING -> store.getString(key.name, key.defaultValue as String?)
            ValueType.INT -> store.getInt(key.name, key.defaultValue as Int)
        }
    }

    private fun set(key: Key, value: Any?) {
        with(store.edit()) {
            when (key.valueType) {
                ValueType.STRING -> putString(key.name, value as String?)
                ValueType.INT -> putInt(key.name, value as Int)
            }
            apply()
        }
    }
}
