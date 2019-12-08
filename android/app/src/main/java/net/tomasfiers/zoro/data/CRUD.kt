// CRUD = Create, Read, Update, Delete data in the database.

package net.tomasfiers.zoro.data

import net.tomasfiers.zoro.data.storage.INITIAL_LOCAL_LIBRARY_VERSION
import net.tomasfiers.zoro.sync.clearSchema

suspend fun DataRepo.clearLocalData() {
    keyValStore.localLibraryVersion = INITIAL_LOCAL_LIBRARY_VERSION
    keyValStore.localSchemaETag = null
    clearSchema()
    database.collectionDAO.clearCollections()
}

suspend fun DataRepo.getCollection(collectionKey: String) =
    database.collectionDAO.get(collectionKey)

fun DataRepo.getChildrenCollections(parentCollectionKey: String?) =
    database.collectionDAO.getChildren(parentCollectionKey)
