/**
 * Android Room "data acces objects".
 */

package net.tomasfiers.zoro.data.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import net.tomasfiers.zoro.data.entities.Collection
import net.tomasfiers.zoro.data.entities.CreatorType
import net.tomasfiers.zoro.data.entities.Field
import net.tomasfiers.zoro.data.entities.ItemType
import net.tomasfiers.zoro.data.entities.ItemTypeCreatorTypeAssociation
import net.tomasfiers.zoro.data.entities.ItemTypeFieldAssociation
import net.tomasfiers.zoro.data.entities.KeyValPair

@Dao
interface KeyValDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(data: KeyValPair)

    @Query("select * from KeyValPair where `key`= :key ")
    suspend fun get(key: String): KeyValPair?
}

@Dao
interface SchemaDAO {

    @Query("delete from ItemType")
    suspend fun clearItemTypes()

    @Query("delete from Field")
    suspend fun clearFields()

    @Query("delete from CreatorType")
    suspend fun clearCreatorTypes()

    @Insert
    suspend fun insertItemType(itemType: ItemType)

    @Insert(onConflict = IGNORE)
    suspend fun insertField(field: Field)

    @Insert(onConflict = IGNORE)
    suspend fun insertCreatorType(creatorType: CreatorType)

    @Insert(onConflict = IGNORE)
    suspend fun insertItemTypeFieldAssociation(obj: ItemTypeFieldAssociation)

    @Insert(onConflict = IGNORE)
    suspend fun insertItemTypeCreatorTypeAssociation(obj: ItemTypeCreatorTypeAssociation)
}

@Dao
interface CollectionDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insert(collection: Collection)

    @Query("select * from Collection where collectionKey = :key")
    suspend fun get(key: String): Collection

    // LiveData queries are automatically executed off the main thread by Room - so we don't mark
    // 'em as async.
    // Also, a gotcha: in SQL you cannot use `= null`, only `is null`.
    @Query("select * from Collection where (parentKey = :parentKey) or (:parentKey is null and parentKey is null)")
    fun getChildren(parentKey: String?): LiveData<List<Collection>>

    @Query("delete from Collection")
    suspend fun clearCollections()
}
