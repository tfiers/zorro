package net.tomasfiers.zorro.sync

import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.entities.Creator
import net.tomasfiers.zorro.data.entities.Item
import net.tomasfiers.zorro.data.entities.ItemCollectionAssociation
import net.tomasfiers.zorro.data.entities.ItemFieldValue
import net.tomasfiers.zorro.data.getValue
import net.tomasfiers.zorro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zorro.zotero_api.remoteLibraryVersion

suspend fun DataRepo.syncItems(remoteLibVersionAtStartSync: Int?) {
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
    val itemCollectionAssocs = mutableListOf<ItemCollectionAssociation>()
    val creators = mutableListOf<Creator>()
    response.body()?.forEach { itemJson ->
        items.add(itemJson.asDomainModel())
        val itemKey = itemJson.key
        val knownAndFilledFields = itemJson.data
            .filter { it.key in args.fieldNames }
            .filter { it.value != "" }
        for ((fieldName, value) in knownAndFilledFields) {
            val itemFieldValue = ItemFieldValue(itemKey, fieldName, value.toString())
            itemFieldValues.add(itemFieldValue)
        }
        itemJson.collectionKeys?.forEach { collectionKey ->
            itemCollectionAssocs.add(ItemCollectionAssociation(collectionKey, itemKey))
        }
        itemJson.creators?.forEach { creatorJson ->
            // `map[key]` syntax in Kotlin automatically yields `null` when key not present.
            creators.add(
                Creator(
                    creatorJson["firstName"],
                    creatorJson["lastName"],
                    creatorJson["name"],
                    itemKey,
                    creatorJson["creatorType"] ?: ""
                )
            )
        }
    }
    database.item.insert(items)
    database.item.insertFieldValues(itemFieldValues)
    database.item.insertItemCollectionAssocs(itemCollectionAssocs)
    database.item.insertCreators(creators)
}
