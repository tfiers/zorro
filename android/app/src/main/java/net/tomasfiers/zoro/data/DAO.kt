// Data acces objects
package net.tomasfiers.zoro.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import net.tomasfiers.zoro.data.domain.Collection

@Dao
interface SchemaDAO {
    @Insert
    suspend fun insertItemType(itemType: ItemType)

    @Insert
    suspend fun insertField(field: Field)
}

@Dao
interface CollectionDAO {
    @Insert(onConflict = REPLACE)
    suspend fun insert(collection: Collection)

    @Query("select * from Collection where `key` = :key")
    suspend fun get(key: String): Collection

    // LiveData queries are automatically executed off the main thread by Room - so we don't mark
    // 'em as async.
    // Also, a gotcha: in SQL you cannot use `= null`, only `is null`.
    @Query("select * from Collection where (parentKey = :parentKey) or (:parentKey is null and parentKey is null)")
    fun getChildren(parentKey: String?): LiveData<List<Collection>>

    @Query("delete from Collection")
    suspend fun clearCollections()
}
