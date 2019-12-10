package net.tomasfiers.zoro.data.storage

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.entities.KeyValPair

const val INITIAL_LOCAL_LIBRARY_VERSION = 0

enum class Key(val dataType: DataType, val defaultValue: Any?) {
    LOCAL_LIBRARY_VERSION(DataType.INT, INITIAL_LOCAL_LIBRARY_VERSION),
    LOCAL_SCHEMA_ETAG(DataType.STRING, null);

    enum class DataType { STRING, INT }
}

// Suspend properties (getters and setters) don't exist in Kotlin.
// So no `keyValStore.localLibraryVersion = x` unfortunately :'(

suspend inline fun <reified T> DataRepo.getValue(key: Key): T =
    when (val value = database.keyValDao.get(key.name)?.value) {
        null -> key.defaultValue as T
        else -> when (key.dataType) {
            Key.DataType.STRING -> value as T
            Key.DataType.INT -> value.toInt() as T
        }
    }

suspend fun DataRepo.setValue(key: Key, value: Any?) =
    database.keyValDao.insert(KeyValPair(key.name, value.toString()))
