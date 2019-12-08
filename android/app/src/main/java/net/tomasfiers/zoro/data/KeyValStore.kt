package net.tomasfiers.zoro.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

const val INITIAL_LOCAL_LIBRARY_VERSION = 0

enum class Key(val valueType: KClass<*>, val defaultValue: Any?) {
    LOCAL_LIBRARY_VERSION(Int::class, INITIAL_LOCAL_LIBRARY_VERSION),
    LOCAL_SCHEMA_ETAG(String::class, null)
}

class KeyValStore(application: Application) {

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: Key): T = getters[key.valueType]?.call(key.name, key.defaultValue) as T

    fun <T> set(key: Key, value: T) {
        with(store.edit()) {
            getSetter(key.valueType, this).call(key.name, value)
            apply()
        }
    }

    private val store = application.getSharedPreferences(
        "net.tomasfiers.zoro.MyKeyValStore", Context.MODE_PRIVATE
    )

    private val getters = mapOf(
        String::class to store::getString,
        Int::class to store::getInt
    )

    private fun getSetter(type: KClass<*>, editor: SharedPreferences.Editor): KFunction<*> {
        return when (type) {
            String::class -> editor::putString
            Int::class -> editor::putInt
            else -> throw Exception("")
        }
    }
}
