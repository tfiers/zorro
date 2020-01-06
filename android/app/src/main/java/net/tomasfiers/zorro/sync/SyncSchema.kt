package net.tomasfiers.zorro.sync

import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.Key
import net.tomasfiers.zorro.data.entities.CreatorType
import net.tomasfiers.zorro.data.entities.Field
import net.tomasfiers.zorro.data.entities.ItemType
import net.tomasfiers.zorro.data.entities.ItemTypeCreatorTypeAssociation
import net.tomasfiers.zorro.data.entities.ItemTypeFieldAssociation
import net.tomasfiers.zorro.data.getPersistentValue
import net.tomasfiers.zorro.data.setPersistentValue
import net.tomasfiers.zorro.zotero_api.SchemaJson


suspend fun DataRepo.syncSchema() {
    val schemaResponse = zoteroAPIClient.getSchema(
        checkIfSchemaUpdated = getPersistentValue(Key.LOCAL_SCHEMA_ETAG)
    )
    if (schemaResponse.code() == 304) {
        // Schema not modified since last check.
        return
    } else {
        syncStatus.value = "Updating schema…"
        clearSchema()
        insertSchema(schemaResponse.body()!!)
        // Only update local schema tag when all db inserts have completed succesfully.
        setPersistentValue(Key.LOCAL_SCHEMA_ETAG, schemaResponse.headers()["ETag"])
    }
}

suspend fun DataRepo.insertSchema(schemaJson: SchemaJson) {
    val friendlyNames = schemaJson.locales.getValue("en-US")
    val itemTypes = mutableListOf<ItemType>()
    val fields = mutableSetOf<Field>()
    val itemTypeFieldAssociations = mutableListOf<ItemTypeFieldAssociation>()
    val creatorTypes = mutableSetOf<CreatorType>()
    val itemTypeCreatorTypeAssociations = mutableListOf<ItemTypeCreatorTypeAssociation>()
    schemaJson.itemTypes.forEach { itemTypeJson ->
        val itemTypeName = itemTypeJson.itemType
        itemTypes.add(
            ItemType(itemTypeName, friendlyNames.itemTypes.getValue(itemTypeName))
        )
        itemTypeJson.fields.forEachIndexed { index, fieldJson ->
            val fieldName = fieldJson.field
            fields.add(
                Field(fieldName, friendlyNames.fields.getValue(fieldName), fieldJson.baseField)
            )
            itemTypeFieldAssociations.add(
                ItemTypeFieldAssociation(itemTypeName, fieldName, index)
            )
        }
        itemTypeJson.creatorTypes.forEach { creatorTypeJson ->
            val creatorTypeName = creatorTypeJson.creatorType
            creatorTypes.add(
                CreatorType(creatorTypeName, friendlyNames.creatorTypes.getValue(creatorTypeName))
            )
            val primary = creatorTypeJson.primary ?: false
            itemTypeCreatorTypeAssociations.add(
                ItemTypeCreatorTypeAssociation(itemTypeName, creatorTypeName, primary)
            )
        }
    }
    database.schema.insertItemTypes(itemTypes)
    database.schema.insertFields(fields.toList())
    database.schema.insertItemTypeFieldAssociations(itemTypeFieldAssociations)
    database.schema.insertCreatorTypes(creatorTypes.toList())
    database.schema.insertItemTypeCreatorTypeAssociations(itemTypeCreatorTypeAssociations)
}

suspend fun DataRepo.clearSchema() {
    database.schema.clearItemTypes() // Foreign keys will propagate deletion to associations.
    database.schema.clearFields()
    database.schema.clearCreatorTypes()
}
