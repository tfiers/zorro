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
import java.util.concurrent.atomic.AtomicInteger

suspend fun DataRepo.syncItems(remoteLibVersionAtStartSync: Int?) {
    syncStatus.value = "Checking for updated items…"
    val itemVersions = zoteroAPIClient.getItemVersions(
        sinceLibraryVersion = getValue(Key.LOCAL_LIBRARY_VERSION)
    )
    val itemIds = itemVersions.keys
    if (itemIds.isNotEmpty()) {
        syncStatus.value = "Downloading ${itemIds.size} items…"
        val fieldNames = database.schema.getFields().map { it.name }
        val chunkedItemIds = itemIds.chunked(MAX_ITEMS_PER_RESPONSE)
        numRequests.value = chunkedItemIds.size
        val numCompletedRequestsAtomic = AtomicInteger(0)
        showProgressBar.value = true
        // Wait until all coroutines launched inside this block have completed.
        coroutineScope {
            chunkedItemIds.forEach { someItemIds ->
                launch {
                    val response = zoteroAPIClient.getItems(someItemIds.joinToString(","))
                    if (response.remoteLibraryVersion != remoteLibVersionAtStartSync) {
                        throw RemoteLibraryUpdatedSignal()
                    }
                    val items = mutableListOf<Item>()
                    val itemFieldValues = mutableListOf<ItemFieldValue>()
                    response.body()?.forEach { itemJson ->
                        items.add(itemJson.asDomainModel())
                        val knownFields = itemJson.data.filter { it.key in fieldNames }
                        for ((fieldName, value) in knownFields) {
                            val itemFieldValue =
                                ItemFieldValue(itemJson.key, fieldName, value.toString())
                            itemFieldValues.add(itemFieldValue)
                        }
                    }
                    database.item.insert(items)
                    database.item.insertFieldValues(itemFieldValues)
                    numCompletedRequests.value = numCompletedRequestsAtomic.incrementAndGet()
                }
            }
        }
        showProgressBar.value = false
    }
}
