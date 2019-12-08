package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.storage.SchemaDAO
import net.tomasfiers.zoro.data.entities.CreatorType
import net.tomasfiers.zoro.data.entities.Field
import net.tomasfiers.zoro.data.entities.ItemType
import net.tomasfiers.zoro.data.entities.ItemTypeCreatorTypeAssociation
import net.tomasfiers.zoro.data.entities.ItemTypeFieldAssociation
import net.tomasfiers.zoro.zotero_api.SchemaJson
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient


suspend fun syncSchema(dataRepo: DataRepo) {
    val schemaResponse = zoteroAPIClient.getSchema(
        checkIfSchemaUpdated = dataRepo.keyValStore.localSchemaETag
    )
    if (schemaResponse.code() == 304) {
        // Schema not modified since last check.
        return
    } else {
        clearSchema(dataRepo)
        insertSchema(schemaResponse.body()!!, dataRepo.database.schemaDAO)
        // Only update local schema tag when all db inserts have completed succesfully.
        dataRepo.keyValStore.localSchemaETag = schemaResponse.headers()["ETag"]
    }
}

suspend fun insertSchema(schemaJson: SchemaJson, schemaDAO: SchemaDAO) {
    val friendlyNames = schemaJson.locales.getValue("en-US")
    schemaJson.itemTypes.forEach { itemTypeJson ->
        val itemTypeName = itemTypeJson.itemType
        schemaDAO.insertItemType(
            ItemType(itemTypeName, friendlyNames.itemTypes.getValue(itemTypeName))
        )
        itemTypeJson.fields.forEach { fieldJson ->
            val fieldName = fieldJson.field
            schemaDAO.insertField(
                Field(fieldName, friendlyNames.fields.getValue(fieldName))
            )
            schemaDAO.insertItemTypeFieldAssociation(
                ItemTypeFieldAssociation(itemTypeName, fieldName)
            )
        }
        itemTypeJson.creatorTypes.forEach { creatorTypeJson ->
            val creatorTypeName = creatorTypeJson.creatorType
            schemaDAO.insertCreatorType(
                CreatorType(
                    creatorTypeName, friendlyNames.creatorTypes.getValue(creatorTypeName)
                )
            )
            schemaDAO.insertItemTypeCreatorTypeAssociation(
                ItemTypeCreatorTypeAssociation(itemTypeName, creatorTypeName)
            )
        }
    }
}

suspend fun clearSchema(dataRepo: DataRepo) {
    dataRepo.database.schemaDAO.clearItemTypes() // Foreign keys will propagate deletion to associations.
    dataRepo.database.schemaDAO.clearFields()
    dataRepo.database.schemaDAO.clearCreatorTypes()
}
