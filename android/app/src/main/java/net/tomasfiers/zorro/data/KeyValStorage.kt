package net.tomasfiers.zorro.data

import net.tomasfiers.zorro.data.entities.KeyValPair

const val INITIAL_LOCAL_LIBRARY_VERSION = 0

enum class Key(val dataType: DataType, val defaultValue: Any?) {
    LOCAL_LIBRARY_VERSION(DataType.INT, INITIAL_LOCAL_LIBRARY_VERSION),
    LOCAL_SCHEMA_ETAG(DataType.STRING, null),
    DEVELOPER_MODE(DataType.BOOLEAN, false);

    enum class DataType { STRING, INT, BOOLEAN }
}

suspend fun DataRepo.setPersistentValue(key: Key, value: Any?) =
    database.keyValPair.insert(KeyValPair(key.name, value.toString()))

suspend inline fun <reified T> DataRepo.getPersistentValue(key: Key): T {
    val stringValue = database.keyValPair.get(key.name)?.value
    return when (stringValue) {
        null -> key.defaultValue
        else -> when (key.dataType) {
            Key.DataType.STRING -> stringValue
            Key.DataType.INT -> stringValue.toInt()
            Key.DataType.BOOLEAN -> stringValue.toBoolean()
        }
    } as T
}
