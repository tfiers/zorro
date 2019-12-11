package net.tomasfiers.zoro.sync

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.Key
import net.tomasfiers.zoro.data.entities.Item
import net.tomasfiers.zoro.data.entities.ItemFieldValue
import net.tomasfiers.zoro.data.getValue
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

suspend fun DataRepo.syncItems(remoteLibVersionAtStartSync: Int?) {
    syncStatus.value = "Checking for updated items…"
    val itemVersions = zoteroAPIClient.getItemVersions(
        sinceLibraryVersion = getValue(Key.LOCAL_LIBRARY_VERSION)
    )
    val itemIds = itemVersions.keys
    val numItemsToDownload = itemIds.size
    if (numItemsToDownload > 0) {
        syncStatus.value = "Downloading ${itemIds.size} items…"
        showProgressBar.value = true
        val fieldNames = database.schema.getFields().map { it.name }
        numObjectsToDownload.value = numItemsToDownload
        val numDownloadedObjectsAtomic = AtomicInteger(0)
        val chunkedItemIds = itemIds.chunked(MAX_ITEMS_PER_RESPONSE)
        val items = mutableListOf<Item>()
        val itemFieldValues = mutableListOf<ItemFieldValue>()
        // Wait until all coroutines launched inside this block have completed.
        coroutineScope {
            chunkedItemIds.forEach { someItemIds ->
                launch {
                    val threadName = Thread.currentThread().name
                    Timber.i("Starting request in $threadName…")
                    val response = zoteroAPIClient.getItems(someItemIds.joinToString(","))
                    Timber.i("Response received in $threadName")
                    if (response.remoteLibraryVersion != remoteLibVersionAtStartSync) {
                        throw RemoteLibraryUpdatedSignal()
                    }
                    response.body()?.forEach { itemJson ->
                        items.add(itemJson.asDomainModel())
                        val knownFields = itemJson.data.filter { it.key in fieldNames }
                        for ((fieldName, value) in knownFields) {
                            val itemFieldValue =
                                ItemFieldValue(itemJson.key, fieldName, value.toString())
                            itemFieldValues.add(itemFieldValue)
                        }
                        numDownloadedObjects.value = numDownloadedObjectsAtomic.incrementAndGet()
                    }
                    Timber.i("Results saved in $threadName")
                }
            }
        }
        Timber.i("Exiting that scope")
        syncStatus.value = "Inserting new items in database…"
        showProgressBar.value = false
        database.item.insert(items)
        database.item.insertFieldValues(itemFieldValues)
    }
}
