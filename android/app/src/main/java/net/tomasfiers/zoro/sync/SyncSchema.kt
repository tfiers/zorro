package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.entities.CreatorType
import net.tomasfiers.zoro.data.entities.Field
import net.tomasfiers.zoro.data.entities.ItemType
import net.tomasfiers.zoro.data.entities.ItemTypeCreatorTypeAssociation
import net.tomasfiers.zoro.data.entities.ItemTypeFieldAssociation
import net.tomasfiers.zoro.data.Key
import net.tomasfiers.zoro.data.getValue
import net.tomasfiers.zoro.data.setValue
import net.tomasfiers.zoro.zotero_api.SchemaJson


suspend fun DataRepo.syncSchema() {
    syncStatus.value = "Updating schema…"
    val schemaResponse = zoteroAPIClient.getSchema(
        checkIfSchemaUpdated = getValue(Key.LOCAL_SCHEMA_ETAG)
    )
    if (schemaResponse.code() == 304) {
        // Schema not modified since last check.
        return
    } else {
        clearSchema()
        insertSchema(schemaResponse.body()!!)
        // Only update local schema tag when all db inserts have completed succesfully.
        setValue(Key.LOCAL_SCHEMA_ETAG, schemaResponse.headers()["ETag"])
    }
}

suspend fun DataRepo.insertSchema(schemaJson: SchemaJson) {
    val friendlyNames = schemaJson.locales.getValue("en-US")
    schemaJson.itemTypes.forEach { itemTypeJson ->
        val itemTypeName = itemTypeJson.itemType
        database.schema.insertItemType(
            ItemType(
                itemTypeName,
                friendlyNames.itemTypes.getValue(itemTypeName)
            )
        )
        itemTypeJson.fields.forEach { fieldJson ->
            val fieldName = fieldJson.field
            database.schema.insertField(
                Field(
                    fieldName,
                    friendlyNames.fields.getValue(fieldName),
                    fieldJson.baseField
                )
            )
            database.schema.insertItemTypeFieldAssociation(
                ItemTypeFieldAssociation(
                    itemTypeName,
                    fieldName
                )
            )
        }
        itemTypeJson.creatorTypes.forEach { creatorTypeJson ->
            val creatorTypeName = creatorTypeJson.creatorType
            database.schema.insertCreatorType(
                CreatorType(
                    creatorTypeName,
                    friendlyNames.creatorTypes.getValue(creatorTypeName)
                )
            )
            database.schema.insertItemTypeCreatorTypeAssociation(
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
    database.schema.clearItemTypes() // Foreign keys will propagate deletion to associations.
    database.schema.clearFields()
    database.schema.clearCreatorTypes()
}
