package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.Key
import net.tomasfiers.zoro.data.entities.ItemDataValue
import net.tomasfiers.zoro.data.entities.ItemItemDataValueAssociation
import net.tomasfiers.zoro.data.getValue
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion

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
        downloadProgress.value = 0f
        var currentItemNr = 1
        val fieldNames = database.schema.getFields().map { it.name }
        itemIds
            .chunked(MAX_ITEMS_PER_RESPONSE)
            .forEach { someItemIds ->
                val response = zoteroAPIClient.getItems(someItemIds.joinToString(","))
                if (response.remoteLibraryVersion != remoteLibVersionAtStartSync) {
                    throw RemoteLibraryUpdatedSignal()
                }
                response.body()?.forEach { itemJson ->
                    database.item.insert(itemJson.asDomainModel())
                    val knownFields = itemJson.data.filter { it.key in fieldNames }
                    for ((fieldName, value) in knownFields) {
                        val itemDataValueId =
                            database.item.insertDataValue(ItemDataValue(value.toString())).toInt()
                        database.item.insertDataValueAssociation(
                            ItemItemDataValueAssociation(itemJson.key, fieldName, itemDataValueId)
                        )
                    }
                    downloadProgress.value = (currentItemNr++).toFloat() / numItemsToDownload
                }
            }
        showProgressBar.value = false
    }
}
