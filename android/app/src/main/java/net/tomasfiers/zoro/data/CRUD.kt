// CRUD = Create, Read, Update, Delete data in the database.

package net.tomasfiers.zoro.data

import net.tomasfiers.zoro.data.storage.INITIAL_LOCAL_LIBRARY_VERSION
import net.tomasfiers.zoro.sync.clearSchema

suspend fun clearLocalData(dataRepo: DataRepo) {
    dataRepo.keyValStore.localLibraryVersion =
        INITIAL_LOCAL_LIBRARY_VERSION
    dataRepo.keyValStore.localSchemaETag = null
    clearSchema(dataRepo)
    dataRepo.database.collectionDAO.clearCollections()
}

suspend fun getCollection(collectionKey: String, dataRepo: DataRepo) =
    dataRepo.database.collectionDAO.get(collectionKey)

fun getChildrenCollections(parentCollectionKey: String?, dataRepo: DataRepo) =
    dataRepo.database.collectionDAO.getChildren(parentCollectionKey)
