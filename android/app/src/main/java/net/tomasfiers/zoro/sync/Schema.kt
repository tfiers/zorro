package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.entities.CreatorType
import net.tomasfiers.zoro.data.entities.Field
import net.tomasfiers.zoro.data.entities.ItemType
import net.tomasfiers.zoro.data.entities.ItemTypeCreatorTypeAssociation
import net.tomasfiers.zoro.data.entities.ItemTypeFieldAssociation
import net.tomasfiers.zoro.zotero_api.SchemaJson
import net.tomasfiers.zoro.zotero_api.zoteroAPIClient


suspend fun DataRepo.syncSchema() {
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

suspend fun DataRepo.insertSchema(schemaJson: SchemaJson) {
    val friendlyNames = schemaJson.locales.getValue("en-US")
    schemaJson.itemTypes.forEach { itemTypeJson ->
        val itemTypeName = itemTypeJson.itemType
        database.schemaDAO.insertItemType(
            ItemType(
                itemTypeName,
                friendlyNames.itemTypes.getValue(itemTypeName)
            )
        )
        itemTypeJson.fields.forEach { fieldJson ->
            val fieldName = fieldJson.field
            database.schemaDAO.insertField(
                Field(
                    fieldName,
                    friendlyNames.fields.getValue(fieldName),
                    fieldJson.baseField
                )
            )
            database.schemaDAO.insertItemTypeFieldAssociation(
                ItemTypeFieldAssociation(
                    itemTypeName,
                    fieldName
                )
            )
        }
        itemTypeJson.creatorTypes.forEach { creatorTypeJson ->
            val creatorTypeName = creatorTypeJson.creatorType
            database.schemaDAO.insertCreatorType(
                CreatorType(
                    creatorTypeName,
                    friendlyNames.creatorTypes.getValue(creatorTypeName)
                )
            )
            database.schemaDAO.insertItemTypeCreatorTypeAssociation(
                ItemTypeCreatorTypeAssociation(
                    itemTypeName,
                    creatorTypeName,
                    creatorTypeJson.primary ?: false
                )
            )
        }
    }
}

suspend fun DataRepo.clearSchema() {
    database.schemaDAO.clearItemTypes() // Foreign keys will propagate deletion to associations.
    database.schemaDAO.clearFields()
    database.schemaDAO.clearCreatorTypes()
}
