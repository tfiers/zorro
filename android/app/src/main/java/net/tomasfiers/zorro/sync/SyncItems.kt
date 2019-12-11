package net.tomasfiers.zorro.sync

import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.entities.Item
import net.tomasfiers.zorro.data.entities.ItemFieldValue
import net.tomasfiers.zorro.data.getValue
import net.tomasfiers.zorro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zorro.zotero_api.remoteLibraryVersion

suspend fun DataRepo.syncItems(remoteLibVersionAtStartSync: Int?) {
    syncStatus.value = "Checking for updated items…"
    val itemVersions = zoteroAPIClient.getItemVersions(
        sinceLibraryVersion = getValue(Key.LOCAL_LIBRARY_VERSION)
    )
    val itemKeys = itemVersions.keys
    if (itemKeys.isNotEmpty()) {
        syncStatus.value = "Downloading ${itemKeys.size} items…"
        val fieldNames = database.schema.getFields().map { it.name }
        val chunkedItemKeys = itemKeys.chunked(MAX_ITEMS_PER_RESPONSE)
        val argsList = chunkedItemKeys.map { someItemKeys ->
            DownloadSomeItemsArgs(someItemKeys, remoteLibVersionAtStartSync, fieldNames)
        }
        makeConcurrentRequests(DataRepo::downloadSomeItems, argsList)
    }
}

class DownloadSomeItemsArgs(
    val itemKeys: List<String>,
    val remoteLibVersionAtStartSync: Int?,
    val fieldNames: List<String>
) : FunctionArgs()

private suspend fun DataRepo.downloadSomeItems(args: DownloadSomeItemsArgs) {
    val response = zoteroAPIClient.getItems(args.itemKeys.joinToString(","))
    if (response.remoteLibraryVersion != args.remoteLibVersionAtStartSync) {
        throw RemoteLibraryUpdatedSignal()
    }
    val items = mutableListOf<Item>()
    val itemFieldValues = mutableListOf<ItemFieldValue>()
    response.body()?.forEach { itemJson ->
        items.add(itemJson.asDomainModel())
        val knownFields = itemJson.data.filter { it.key in args.fieldNames }
        for ((fieldName, value) in knownFields) {
            val itemFieldValue =
                ItemFieldValue(itemJson.key, fieldName, value.toString())
            itemFieldValues.add(itemFieldValue)
        }
    }
    database.item.insert(items)
    database.item.insertFieldValues(itemFieldValues)
}
