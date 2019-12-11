/**
 * Android Room "DAO's", or data acces objects.
 * Semi-automatically generate SQL queries.
 */

package net.tomasfiers.zorro.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import net.tomasfiers.zorro.data.entities.*
import net.tomasfiers.zorro.data.entities.Collection

@Dao
interface KeyValPairDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(data: KeyValPair)

    @Query("select * from KeyValPair where `key`= :key ")
    suspend fun get(key: String): KeyValPair?
}


@Dao
interface SchemaDao {

    @Query("delete from ItemType")
    suspend fun clearItemTypes()

    @Query("delete from Field")
    suspend fun clearFields()

    @Query("delete from CreatorType")
    suspend fun clearCreatorTypes()

    @Insert
    suspend fun insertItemTypes(itemType: List<ItemType>)

    @Insert
    suspend fun insertFields(field: List<Field>)

    @Insert
    suspend fun insertCreatorTypes(creatorType: List<CreatorType>)

    @Insert
    suspend fun insertItemTypeFieldAssociations(obj: List<ItemTypeFieldAssociation>)

    @Insert
    suspend fun insertItemTypeCreatorTypeAssociations(obj: List<ItemTypeCreatorTypeAssociation>)

    @Query("select * from Field")
    suspend fun getFields(): List<Field>
}


@Dao
interface CollectionDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(collections: List<Collection>)

    @Query("select * from Collection where `key` = :key")
    suspend fun get(key: String): Collection

    // LiveData queries are automatically executed off the main thread by Room - so we don't mark
    // 'em as async.
    // Also, a gotcha: in SQL you cannot use `= null`, only `is null`.
    @Query("select * from Collection where (parentKey = :parentKey) or (:parentKey is null and parentKey is null)")
    fun getChildren(parentKey: String?): LiveData<List<Collection>>
}


@Dao
interface ItemDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(items: List<Item>)

    @Insert
    suspend fun insertFieldValues(data: List<ItemFieldValue>)

    @Insert
    suspend fun insertItemCollectionAssocs(assocs: List<ItemCollectionAssociation>)

    //@Query("select * from Item where ")
    //fun getChildren(parentCollectionKey: String?): LiveData<List<ItemWithReferences>>
}
