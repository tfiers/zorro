/**
 * CRUD = Create, Read, Update, Delete data in the database.
 */

package net.tomasfiers.zorro.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Note that this `clearAllTables` is not a suspending function; It fires an asynchronous task
 * without callback. There is thus no way to know when the database is actually cleared (other than
 * manually checking if data is still present).
 */
suspend fun DataRepo.clearLocalData() =
    withContext(Dispatchers.IO) { database.clearAllTables() }

suspend fun DataRepo.getCollection(collectionKey: String) =
    database.collection.get(collectionKey)

fun DataRepo.getChildrenCollections(parentCollectionKey: String?) =
    database.collection.getChildren(parentCollectionKey)
