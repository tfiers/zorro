package net.tomasfiers.zoro.data

import androidx.lifecycle.MutableLiveData
import net.tomasfiers.zoro.BuildConfig
import net.tomasfiers.zoro.data.domain.CreatorType
import net.tomasfiers.zoro.data.domain.Field
import net.tomasfiers.zoro.data.domain.ItemType
import net.tomasfiers.zoro.data.domain.ItemTypeCreatorTypeAssociation
import net.tomasfiers.zoro.data.domain.ItemTypeFieldAssociation
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.SchemaJson
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient
import org.threeten.bp.Instant
import org.threeten.bp.Instant.now

class RemoteLibraryUpdatedSignal : Exception()

class DataRepository(
    private val database: ZoroDatabase,
    private val keyValStore: KeyValStore
) {

    val isSyncing = MutableLiveData<Boolean>(false)
    val syncError = MutableLiveData<String?>(null)
    var lastSyncTime: Instant? = null

    private var remoteLibraryVersion: Int? = null

    fun getChildrenCollections(parentCollectionKey: String?) =
        database.collectionDAO.getChildren(parentCollectionKey)

    suspend fun getCollection(collectionKey: String) = database.collectionDAO.get(collectionKey)

    suspend fun clearLocalData() {
        keyValStore.localLibraryVersion = INITIAL_LOCAL_LIBRARY_VERSION
        keyValStore.localSchemaETag = null
        clearSchema()
        database.collectionDAO.clearCollections()
    }

    suspend fun syncLibrary() {
        isSyncing.value = true
        syncError.value = null
        // We handle updates to the remote library *while* we are synching, by checking the remote
        // library version after each request, and restarting the synching procedure if necessary.
        retrySyncLoop@ while (true) {
            try {
                syncSchema()
                syncCollections()
                break@retrySyncLoop
            } catch (e: RemoteLibraryUpdatedSignal) {
                continue@retrySyncLoop
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    throw e
                } else {
                    syncError.value = e.message
                    break@retrySyncLoop
                }
            }
        }
        // Note: we do not mark sync as succesful (and local library version as updated) until all
        // requests and database inserts have completed.
        keyValStore.localLibraryVersion = remoteLibraryVersion ?: 0
        lastSyncTime = now()
        isSyncing.value = false
    }


    private suspend fun syncSchema() {
        val schemaResponse = zoteroAPIClient.getSchema(
            checkIfSchemaUpdated = keyValStore.localSchemaETag
        )
        if (schemaResponse.code() == 304) {
            // Schema not modified since last check.
            return
        } else {
            clearSchema()
            insertSchema(schemaResponse.body()!!)
            // Only update local schema tag when all db inserts have completed succesfully.
            keyValStore.localSchemaETag = schemaResponse.headers()["ETag"]
        }
    }

    private suspend fun insertSchema(schemaJson: SchemaJson) {
        val friendlyNames = schemaJson.locales.getValue("en-US")
        schemaJson.itemTypes.forEach { itemTypeJson ->
            val itemTypeName = itemTypeJson.itemType
            database.schemaDAO.insertItemType(
                ItemType(itemTypeName, friendlyNames.itemTypes.getValue(itemTypeName))
            )
            itemTypeJson.fields.forEach { fieldJson ->
                val fieldName = fieldJson.field
                database.schemaDAO.insertField(
                    Field(fieldName, friendlyNames.fields.getValue(fieldName))
                )
                database.schemaDAO.insertItemTypeFieldAssociation(
                    ItemTypeFieldAssociation(itemTypeName, fieldName)
                )
            }
            itemTypeJson.creatorTypes.forEach { creatorTypeJson ->
                val creatorTypeName = creatorTypeJson.creatorType
                database.schemaDAO.insertCreatorType(
                    CreatorType(
                        creatorTypeName, friendlyNames.creatorTypes.getValue(creatorTypeName)
                    )
                )
                database.schemaDAO.insertItemTypeCreatorTypeAssociation(
                    ItemTypeCreatorTypeAssociation(itemTypeName, creatorTypeName)
                )
            }
        }
    }

    private suspend fun clearSchema() {
        database.schemaDAO.clearItemTypes() // Foreign keys will propagate deletion to associations.
        database.schemaDAO.clearFields()
        database.schemaDAO.clearCreatorTypes()
    }

    private suspend fun syncCollections() {
        val collectionVersionsResponse = zoteroAPIClient.getCollectionVersions(
            sinceLibraryVersion = keyValStore.localLibraryVersion
        )
        remoteLibraryVersion = collectionVersionsResponse.remoteLibraryVersion
        val collectionIds = collectionVersionsResponse.body()?.keys ?: emptyList<String>()
        collectionIds.chunked(MAX_ITEMS_PER_RESPONSE).forEach { idListChunk ->
            val jsonCollectionsResponse =
                zoteroAPIClient.getCollections(idListChunk.joinToString(","))
            if (jsonCollectionsResponse.remoteLibraryVersion != remoteLibraryVersion) {
                throw RemoteLibraryUpdatedSignal()
            }
            jsonCollectionsResponse.body()?.forEach {
                database.collectionDAO.insert(it.asDomainModel())
            }
        }
    }

    private suspend fun syncItems() {

    }
}
