/**
 * CRUD = Create, Read, Update, Delete data in the database.
 */

package net.tomasfiers.zoro.data

import net.tomasfiers.zoro.sync.clearSchema

suspend fun DataRepo.clearLocalData() {
    setValue(Key.LOCAL_LIBRARY_VERSION, INITIAL_LOCAL_LIBRARY_VERSION)
    setValue(Key.LOCAL_SCHEMA_ETAG, null)
    clearSchema()
    database.collection.clearAll()
}

suspend fun DataRepo.getCollection(collectionKey: String) =
    database.collection.get(collectionKey)

fun DataRepo.getChildrenCollections(parentCollectionKey: String?) =
    database.collection.getChildren(parentCollectionKey)
